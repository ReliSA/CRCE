package cz.zcu.kiv.crce.resolver.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.MetadataDao;
import cz.zcu.kiv.crce.metadata.dao.filter.CapabilityFilter;
import cz.zcu.kiv.crce.metadata.dao.filter.ResourceFilter;
import cz.zcu.kiv.crce.resolver.Operator;
import cz.zcu.kiv.crce.resolver.ResourceLoader;
import cz.zcu.kiv.crce.resolver.optimizer.CostFunction;
import cz.zcu.kiv.crce.resolver.optimizer.CostFunctionRepository;
import cz.zcu.kiv.crce.resolver.optimizer.NsResultOptimizer;
import cz.zcu.kiv.crce.resolver.optimizer.OptimizationMode;
import cz.zcu.kiv.crce.resolver.optimizer.ResultOptimizer;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = ResourceLoader.class)
@ParametersAreNonnullByDefault
public class ResourceLoaderImpl implements ResourceLoader {

    private static final Logger logger = LoggerFactory.getLogger(ResourceLoaderImpl.class);

    @ServiceDependency private volatile MetadataDao metadataDao;
    @ServiceDependency private volatile MetadataFactory metadataFactory; // NOPMD will be used
    @ServiceDependency private volatile CostFunctionRepository costFunctionRepository;
    @ServiceDependency(required = false) private volatile ResultOptimizer optimizer;

    @Override
    @Nonnull
    public List<Resource> getResources(Repository repository, Requirement requirement) throws IOException {
        return getResources(repository, Collections.singleton(requirement));
    }

    @Nonnull
    @Override
    public List<Resource> getResources(Repository repository, Set<Requirement> requirements) throws IOException {
        return getResources(repository, requirements, Operator.AND);
    }

    @Nonnull
    @Override
    public List<Resource> getResources(Repository repository, Set<Requirement> requirements, Operator op) throws IOException {
        List<Resource> resources = Collections.emptyList();
        try {
            Requirement optRequirement = extractAndRemoveOptimizationRequirement(requirements);

            ResourceFilter filter = buildFilter(requirements, op);
            resources = metadataDao.loadResources(repository, filter);

            if(optimizer != null && optRequirement != null) {
                CostFunction costFunction = extractCostFunction(optRequirement);
                OptimizationMode mode = extractOptimizationMode(optRequirement);

                if(costFunction != null) {
                    logger.debug("Optimizing resultset by cost function: {}", costFunction.getId());
                    resources = optimizer.optimizeResult(requirements, resources, costFunction, mode);
                } else {
                    logger.info("Optimization request has been sent, yet no or unknown optimization function was specified!");
                }
            }

        } catch (IOException e) {
            logger.error("Could not load resources for requirements ({})", requirements.toString());
            logger.error("Stacktrace: ", e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getResources(requirement={}) returns {}", requirements.toString(), resources.size());
        }
        return resources;
    }

    /**
     *
     * @param optimizationRequest request specifying the cost function
     * @return cost function instance based on the optimizationRequest specification or null
     */
    @Nullable
    private CostFunction extractCostFunction(Requirement optimizationRequest) {
        //TODO Requirement should implement AttributeProvider interface to avoid this
        List<Attribute<String>> id = optimizationRequest.getAttributes(NsResultOptimizer.ATTRIBUTE__FUNCTION_ID);

        return !id.isEmpty() ? costFunctionRepository.findOne(id.get(0).getValue()).createInstance(optimizationRequest) : null;
    }

    /**
     *
     * @param requirement request specifying the optimization mode
     * @return specified optimization mode or MIN (default)
     */
    @Nonnull
    private OptimizationMode extractOptimizationMode(Requirement requirement) {
        String modeString = requirement.getDirective(NsResultOptimizer.DIRECTIVE__MODE);
        OptimizationMode mode = OptimizationMode.parse(modeString);

        return mode != null ? mode : OptimizationMode.MIN;
    }

    /**
     * Searches set of requirements whether they contain optimization request.
     * @param requirements requirements to be searched
     * @return found optimization request or null
     */
    @Nullable
    private Requirement extractAndRemoveOptimizationRequirement(Set<Requirement> requirements) {
        Iterator<Requirement> it = requirements.iterator();
        Requirement tmp;
        while (it.hasNext()) {
            tmp = it.next();
            if(Objects.equals(NsResultOptimizer.NAMESPACE__RESULT_OPTIMIZER, tmp.getNamespace())) {
                it.remove();
                return tmp;
            }
        }

        return null;
    }

    private ResourceFilter buildFilter(Set<Requirement> requirements, Operator op) {
        ResourceFilter filter = new ResourceFilter();

        switch (op) {
            case AND:
                filter.setOperator(cz.zcu.kiv.crce.metadata.dao.filter.Operator.AND);
                break;
            case OR:
                filter.setOperator(cz.zcu.kiv.crce.metadata.dao.filter.Operator.OR);
                break;
            default:
                throw new RuntimeException("Invalid state!");
        }

        for (Requirement req : requirements) {
            CapabilityFilter cap = new CapabilityFilter(req.getNamespace());

            String operator = req.getDirective("operator");
            if (operator == null || operator.equals("and")) {
                cap.setOperator(cz.zcu.kiv.crce.metadata.dao.filter.Operator.AND);
            } else if (operator.equals("or")) {
                cap.setOperator(cz.zcu.kiv.crce.metadata.dao.filter.Operator.OR);
            }

            cap.addAttributes(req.getAttributes());
            filter.addCapabilityFilter(cap);
        }

        return filter;
    }
}
