package cz.zcu.kiv.crce.resolver.internal.optimizer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.Inject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.resolver.optimizer.CostFunctionFactory;
import cz.zcu.kiv.crce.resolver.optimizer.CostFunctionRepository;

/**
 * Implementation of {@link CostFunctionRepository} which displays {@link CostFunctionFactory} instances
 * registered as OSGi services.
 *
 * Date: 31.5.16
 *
 * @author Jakub Danek
 */
@Component(provides = CostFunctionRepository.class)
@ParametersAreNonnullByDefault
public class OsgiCostFunctionRepository implements CostFunctionRepository {

    private static final Logger logger = LoggerFactory.getLogger(OsgiCostFunctionRepository.class);

    @Inject
    private volatile BundleContext context;

    @Override
    public List<CostFunctionFactory> list() {
        List<CostFunctionFactory> repos = new LinkedList<>();

        try {
            Collection<ServiceReference<CostFunctionFactory>> refs = context.getServiceReferences(CostFunctionFactory.class, null);
            logger.debug("{} cost function descriptors found!", refs.size());

            for (ServiceReference<CostFunctionFactory> ref : refs) {
                repos.add(context.getService(ref));
            }


        } catch (InvalidSyntaxException e) {
            logger.error("Could not list CostFunctionDescriptor references!", e);
        }

        return repos;
    }

    @Override
    public CostFunctionFactory findOne(String id) {
        CostFunctionFactory desc = null;
        try {
            String filter = "(" + CostFunctionFactory.ID + "=" + id + ")";
            Collection<ServiceReference<CostFunctionFactory>> refs = context.getServiceReferences(CostFunctionFactory.class, filter);

            if(!refs.isEmpty()) {
                desc = context.getService(refs.iterator().next());
            }
        } catch (InvalidSyntaxException e) {
            logger.error("Could not find CostFunctionDescriptor with ID {}!", id);
            logger.error(e.getMessage(), e);
        }

        return desc;
    }
}
