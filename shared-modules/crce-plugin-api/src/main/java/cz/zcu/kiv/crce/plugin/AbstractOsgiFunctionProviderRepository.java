package cz.zcu.kiv.crce.plugin;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link FunctionProviderRepository} which displays {@link FunctionProvider} instances
 * registered as OSGi services.
 *
 * Date: 31.5.16
 *
 * @author Jakub Danek
 */
@ParametersAreNonnullByDefault
public class AbstractOsgiFunctionProviderRepository<T extends FunctionProvider> implements FunctionProviderRepository<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractOsgiFunctionProviderRepository.class);

    private volatile BundleContext context;

    private Class<T> clazz;

    public AbstractOsgiFunctionProviderRepository(Class<T> clazz) {
        this.clazz = clazz;
        this.context = FrameworkUtil.getBundle(AbstractOsgiFunctionProviderRepository.class).getBundleContext();
    }

    @Override
    public List<T> list() {
        List<T> repos = new LinkedList<>();

        try {
            Collection<ServiceReference<T>> refs = context.getServiceReferences(clazz, null);
            logger.debug("{} cost function descriptors found!", refs.size());

            for (ServiceReference<T> ref : refs) {
                repos.add(context.getService(ref));
            }


        } catch (InvalidSyntaxException e) {
            logger.error("Could not list CostFunctionDescriptor references!", e);
        }

        return repos;
    }

    @Override
    public T findOne(String id) {
        T desc = null;
        try {
            String filter = "(" + FunctionProvider.ID + "=" + id + ")";
            Collection<ServiceReference<T>> refs = context.getServiceReferences(clazz, filter);

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
