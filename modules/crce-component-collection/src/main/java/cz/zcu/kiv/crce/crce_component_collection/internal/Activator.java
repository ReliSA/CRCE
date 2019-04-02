package cz.zcu.kiv.crce.crce_component_collection.internal;

import cz.zcu.kiv.crce.compatibility.service.CompatibilitySearchService;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.repository.Store;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Activator extends DependencyActivatorBase {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    private static volatile Activator instance;

    private volatile BundleContext bundleContext;
    private volatile MetadataService metadataService;

    public static Activator instance() {
        if (instance == null) {
            throw new IllegalStateException("Activator instance is null.");
        }
        return instance;
    }

    @Nonnull
    public Map<String, String> getRepositories() {
        Map<String, String> stores = new HashMap<>();

        Collection<ServiceReference<Store>> serviceReferences;
        try {
            serviceReferences = bundleContext.getServiceReferences(Store.class, null);
        } catch (InvalidSyntaxException e) {
            logger.error("Invalid filter.", e); // this should not happen
            return stores;
        }

        if (serviceReferences == null) {
            logger.trace("No stores found.");
            return stores;
        }

        for (ServiceReference<Store> serviceReference : serviceReferences) {
            String id = (String) serviceReference.getProperty("id");
            String name = (String) serviceReference.getProperty("name");
            if (id != null) {
                stores.put(id, name != null ? name : id);
            }
        }
        return stores;
    }

    public Store getStore(String repositoryId) {
        String filter = "(id=" + repositoryId + ")";

        Collection<ServiceReference<Store>> serviceReferences;
        try {
            serviceReferences = bundleContext.getServiceReferences(Store.class, filter);
        } catch (InvalidSyntaxException ex) {
            logger.error("Invalid filter: " + filter);
            return null;
        }

        if (serviceReferences == null || serviceReferences.isEmpty()) {
            logger.warn("Store not found for repository ID: {}", repositoryId);
            return null;
        }

        if (serviceReferences.size() > 1) {
            logger.warn("More than one stores found for repository ID: {}, using the first one.", repositoryId);
        }

        return bundleContext.getService(serviceReferences.iterator().next());
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Workaround for providing DM components.")
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        instance = this;

        manager.add(createComponent().setImplementation(this)
                .add(createServiceDependency().setService(MetadataFactory.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataService.class).setRequired(true))
                .add(createServiceDependency().setService(CompatibilitySearchService.class).setRequired(false)));
        logger.debug("Component collection activator initialized.");
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do
    }
}