package cz.zcu.kiv.crce.optimizer.internal.result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.Property;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.FunctionProvider;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.repository.Store;
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
                @Property(name = FunctionProvider.ID, value = IlpLPSolveTransitiveOptimizer.ID),
                @Property(name = FunctionProvider.DESCRIPTION, value = IlpLPSolveTransitiveOptimizer.DESCRIPTION)
            })
public class IlpLPSolveTransitiveOptimizer extends IlpLPSolveOptimizer {

    public static final String ID = "ro-ilp-transitive-dependencies";
    public static final String DESCRIPTION = "Optimizes returend result set considering the query requirements and their transitive dependencies.";

    protected static final int CONSTRAINT_RIGHT_SIDE_TRANSITIVE = 0;

    @ServiceDependency
    private Store store; //DI

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }


    private Map<String, List<Requirement>> transitive = new HashMap<>();

    @Override
    @Nonnull
    public List<Resource> optimizeResult(Set<Requirement> requirements, List<Resource> fullSet, CostFunction cost, OptimizationMode mode) {
        try {
            String id = UUID.randomUUID().toString();
            setTransitive(id, fetchTransitiveDependencyCandidates(fullSet));
            Set<Requirement> fullReqs = new HashSet<>(requirements);
            fullReqs.addAll(getTransitive(id));
            return this.runOptimizeResult(id, fullReqs, fullSet, cost, mode);
        } catch (IOException e) {
            return new ArrayList<>();
        }

    }

    @Override
    protected void addConstraintRow(String id, LpSolve lpSolve, double[] row, Requirement req) throws LpSolveException {
        if(getTransitive(id).contains(req)) {
            int requiredByCount = 0;

            for (double aRow : row) {
                if (aRow == 1) requiredByCount--;
            }

            for (int i = 0; i < row.length; i++) {
                if(row[i] == -1) row[i] = requiredByCount;
            }

            lpSolve.addConstraint(row, LpSolve.LE, CONSTRAINT_RIGHT_SIDE_TRANSITIVE);
        } else {
            super.addConstraintRow(id, lpSolve, row, req);
        }
    }

    @Override
    protected int getConstraintCellValue(String id, Requirement req, Resource res) {
        int provides = super.getConstraintCellValue(id, req, res);
        if(getTransitive(id).contains(req)) {
            if(provides > 0) {
                return -1;
            }

            if(res.getRequirements(req.getNamespace()).contains(req)) {
                return 1;
            }

            return 0;
        } else {
            return provides;
        }
    }

    protected List<Requirement> fetchTransitiveDependencyCandidates(List<Resource> basicSet) throws IOException {
        Queue<Resource> toProcess = new LinkedList<>(basicSet);

        List<Requirement> transitive = new LinkedList<>();
        Resource tmp;
        List<Resource> found;
        while (!toProcess.isEmpty()) {
            tmp = toProcess.remove();
            if(!tmp.getRequirements().isEmpty()) {
                found = store.getPossibleResources(new HashSet<>(tmp.getRequirements()), true);
                toProcess.addAll(found);
                basicSet.addAll(found);
                transitive.addAll(tmp.getRequirements());
            }
        }

        return transitive;
    }

    protected List<Requirement> getTransitive(String id) {
        return this.transitive.get(id);
    }

    protected void setTransitive(String id, List<Requirement> toSet) {
        this.transitive.put(id, toSet);
    }
}
