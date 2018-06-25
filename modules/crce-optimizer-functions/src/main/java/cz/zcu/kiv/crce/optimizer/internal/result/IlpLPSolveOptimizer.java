package cz.zcu.kiv.crce.optimizer.internal.result;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.plugin.FunctionProvider;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.resolver.optimizer.CostFunction;
import cz.zcu.kiv.crce.resolver.optimizer.OptimizationMode;
import cz.zcu.kiv.crce.resolver.optimizer.ResultOptimizer;

/**
 * LpSolve based implementation of {@link ResultOptimizer} interface using integer linear programming
 * model to optimize the results.
 *
 * Resources represent columns, class-level requirements represent rows. Cost function is self-explanatory.
 *
 * Detailed description of the method can be found in the
 * <a href="http://link.springer.com/chapter/10.1007/978-3-662-49192-8_37">
 *     SOFSEM 2016 article
 * </a>
 *
 *
 * Date: 17.6.16
 *
 * @author Jakub Danek
 */
@Component(provides = {Plugin.class, ResultOptimizer.class},
            properties = {
                @Property(name = FunctionProvider.ID, value = IlpLPSolveOptimizer.ID),
                @Property(name = FunctionProvider.DESCRIPTION, value = IlpLPSolveOptimizer.DESCRIPTION)
            })
public class IlpLPSolveOptimizer extends AbstractPlugin implements ResultOptimizer {

    public static final String ID = "ro-ilp-direct-dependencies";
    public static final String DESCRIPTION = "Optimizes returend result set considering only the query requirements, not transitive dependencies.";

    private static final Logger logger = LoggerFactory.getLogger(IlpLPSolveOptimizer.class);

    protected static final double CONSTRAINT_RIGHT_SIDE = 1.0;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public List<Resource> optimizeResult(Set<Requirement> requirements, List<Resource> fullSet, CostFunction cost, OptimizationMode mode) {
        return runOptimizeResult(null, requirements, fullSet, cost, mode);
    }

    public List<Resource> runOptimizeResult(String id, Set<Requirement> requirements, List<Resource> fullSet, CostFunction cost, OptimizationMode mode) {
        if(fullSet.isEmpty()) {
            logger.info("No candidates, nothing to optimize.");
            return fullSet;
        }

        logger.info("Attempting result optimization.");
        LpSolve lp = null;
        try {
            lp = createLpProblem(id, fullSet.size(), mode);
            lp = buildConstraints(id, lp, new LinkedList<>(requirements), fullSet, cost);
            lp.printLp();
            lp.solve();
            lp.printConstraints(fullSet.size());
            List<Resource> toReturn = new LinkedList<>();
            double[] res = lp.getPtrVariables();
            for (int i = 0; i < res.length; i++) {
                if(((int) res[i]) == 1) {
                    toReturn.add(fullSet.get(i));
                }
            }

            logger.info("Result optimization successful");
            return toReturn;

        } catch (LpSolveException e) {
            logger.error("Error during result optimization.", e);
            return fullSet;
        } catch (Exception e) {
            logger.error("Unexpected error" + e.getMessage(), e);
            return fullSet;
        } finally {
            if(lp != null) {
                lp.deleteLp();
            }
        }
    }

    /**
     * Create {@link LpSolve} instance with general configuration such as min/max mode for the
     * optimization function etc.
     *
     *
     * @param id
     * @param columns     number of resources in the full result set
     * @param mode optimization mode (min/max)
     * @return new LpSolve instance
     * @throws LpSolveException
     */
    protected LpSolve createLpProblem(String id, int columns, OptimizationMode mode) throws LpSolveException {
        LpSolve solve = LpSolve.makeLp(0, columns);

        switch (mode) {
            case MAX:
                logger.debug("Optimization mode: MAX");
                solve.setMaxim();
                break;
            case MIN:
            default:
                logger.debug("Optimization mode: MIN");
                solve.setMinim();
                break;
        }
        //additional general config comes here

        return solve;
    }

    /**
     *
     *
     * @param id
     * @param lpSolve   problem instance
     * @param requirements list of requirements (constraints, rows)
     * @param fullSet list of candidate resources (columns)
     * @param cost cost function
     * @return problem instance with constraints set
     * @throws LpSolveException
     */
    protected LpSolve buildConstraints(String id, LpSolve lpSolve, List<Requirement> requirements, List<Resource> fullSet, CostFunction cost) throws LpSolveException {
        final int columns = fullSet.size() + 1;
        final int constraints = requirements.size();
        logger.debug("Optimization - columns: {}, constraints: {}", columns, constraints);
        final double costs[] = new double[columns];

        Resource res;
        Requirement req;
        double row[];
        for (int i = 0; i < constraints; i++) {
            row = new double[columns];
            req = requirements.get(i);

            //all indexes for lpSolve must be incremented by 1
            //lpSolve starts with indexes at 1 (not 0 as java)
            for (int j = 0; j < fullSet.size(); j++) {
                res = fullSet.get(j);

                if(i == 0) {
                    costs[j + 1] = cost.getCost(res);
                    lpSolve.setBinary(j + 1, true);
                }

                row[j + 1] = getConstraintCellValue(id, req, res);
            }

            addConstraintRow(id, lpSolve, row, req);
        }

        lpSolve.setObjFn(costs);

        return lpSolve;
    }

    protected void addConstraintRow(String id, LpSolve lpSolve, double[] row, Requirement req) throws LpSolveException {
        lpSolve.addConstraint(row, LpSolve.EQ, CONSTRAINT_RIGHT_SIDE);
    }

    /**
     * Checks whether the resource provides the requirement.
     *
     *
     * @param id
     * @param req requirement
     * @param res resource
     * @return 1 if resource provides the requirement, 0 otherwise
     */
    protected int getConstraintCellValue(String id, Requirement req, Resource res) {
        for (Capability capability : res.getCapabilities(req.getNamespace())) {
            if(compare(req, capability)) {
                return 1;
            }
        }

        return 0;
    }

    /**
     * Compares requirement and capability in order to determine whether they are a match (represent the
     * same feature).
     *
     * @param req requirement to be checked
     * @param cap capability to be checked
     * @return true if req and cap match
     */
    protected boolean compare(Requirement req, Capability cap) {
        logger.debug("Optimization - comparing requirement with capability: {} - {}", req.getNamespace(), cap.getId());
        if(!req.getNamespace().equals(cap.getNamespace())) {
            logger.debug("Optimization - non-matching namespace: {} - {}", req.getNamespace(), cap.getNamespace());
            return false;
        }

        //restricts set of attributes meaningful to compatibility check - this needs
        //further analysis.
        Set<String> allowedAttributes = new HashSet<>(Arrays.asList(new String[]{"static", "name", "paramTypes", "returnType", "constructor"}));

        boolean canDo = true;
        for (Attribute attribute : req.getAttributes()) {

            if(!allowedAttributes.contains(attribute.getName())) {
                continue;
            }

            if(!Objects.equals(cap.getAttributeValue(attribute.getAttributeType()), attribute.getValue())) {
                logger.debug("Optimization - non-matching attribute: {} - {}", cap.getAttributeValue(attribute.getAttributeType()), attribute.getValue());
                canDo = false;
                break;
            }
        }

        if(canDo) {
            for (Requirement reqChild : req.getChildren()) {
                for (Capability capChild : cap.getChildren()) {
                    canDo = compare(reqChild, capChild);
                    if(canDo) {
                        break;
                    }
                }

                if(!canDo) {
                    break;
                }
            }
        }

        return canDo;
    }


}
