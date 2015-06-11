package cz.zcu.kiv.crce.webui.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.ServiceUnavailableException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.service.CompatibilitySearchService;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.webservices.indexer.internal.WebservicesDescription;

/**
 * Activator of this bundle
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@SuppressWarnings("FinalClass")
public final class Activator extends DependencyActivatorBase {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    private static volatile Activator instance;

    // injected by dependency manager:
    private volatile BundleContext bundleContext;
    private volatile MetadataFactory metadataFactory;
    private volatile ResourceDAO resourceDAO;
    private volatile PluginManager pluginManager;
    private volatile SessionRegister sessionRegister;
    private volatile MetadataService metadataService;
    private volatile WebservicesDescription webservicesDescription;
    private volatile CompatibilitySearchService compatibilityService;

    public static Activator instance() {
        if (instance == null) {
            throw new IllegalStateException("Activator instance is null.");
        }
        return instance;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public SessionRegister getSessionRegister() {
        if (sessionRegister == null) {
            throw new IllegalStateException("sessionRegister is null.");
        }
        return sessionRegister;
    }

    public ResourceDAO getResourceDAO() {
        return resourceDAO;
    }

    public MetadataFactory getMetadataFactory() {
        return metadataFactory;
    }

    public WebservicesDescription getWebservicesDescription() {
        return webservicesDescription;
    }

    /**
     *
     * @return Map of repository ID to repository name.
     */
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

    public CompatibilitySearchService getCompatibilityService() {
        if(compatibilityService != null) {
            return compatibilityService;
        } else {
            throw new ServiceUnavailableException("This installation does not support compatibility services!", "");
        }
    }

    public boolean isCompatibilityServicePresent() {
        return compatibilityService != null;
    }

    public Buffer getBuffer(HttpServletRequest req) {
        if (req == null) {
            return null;
        }

        String sid = req.getSession(true).getId();
        return sessionRegister.getSessionData(sid).getBuffer();
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Workaround for providing DM components.")
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        instance = this;

        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(WebservicesDescription.class).setRequired(true))
                .add(createServiceDependency().setService(SessionRegister.class).setRequired(true))
                .add(createServiceDependency().setService(PluginManager.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataFactory.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataService.class).setRequired(true))
                .add(createServiceDependency().setService(CompatibilitySearchService.class).setRequired(false)) // FIXME 'not required' is only a temporary solution to make the component startable
        );


        logger.debug("WebUI activator initialized.");
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do
    }
}
