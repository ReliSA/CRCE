package cz.zcu.kiv.crce.repository.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Dictionary;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.dao.RepositoryDAO;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexerService;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.validation.MetadataValidator;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.resolver.ResourceLoader;

/**
 * Activator of this bundle.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase implements ManagedService {

    public static final String PID = "cz.zcu.kiv.crce.repository";

    public static final String STORE_URI = "store.uri";

    private volatile DependencyManager dependencyManager; /* injected by dependency manager */
    private volatile BundleContext bundleContext;         /* injected by dependency manager */

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    @Override
    public void init(BundleContext bc, DependencyManager dm) throws Exception {

        dm.add(createComponent()
                .setImplementation(this)
                .add(createConfigurationDependency().setPid(PID))
                );

        dm.add(createComponent()
                .setInterface(SessionRegister.class.getName(), null)
                .setImplementation(SessionRegisterImpl.class)
                );

        dm.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(PriorityActionHandler.class)
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                );
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        logger.debug("Updating Repository Activator configuration: {}", properties);

        if (properties == null) {
            logger.warn("Repository configuration is empty!");
            return;
        }

        if (bundleContext.getServiceReference(Store.class.getName()) != null) {
            logger.warn("Store URI reconfiguration on runtime is not supported");
            return;
        }

        String path = (String) properties.get(STORE_URI);

        URI uri = null;
        File file = null;
        try {
            uri = new URI(path);
            if (uri.getScheme() == null) {
                file = new File(path);
            } else if ("file".equals(uri.getScheme())) {
                file = new File(uri);
            } else {
                throw new ConfigurationException(STORE_URI, "No Store implementation for given URI scheme: " + uri.getScheme());
            }
        } catch (URISyntaxException ex) {
            // TODO verify this usecase and correctness
            file = new File(path);
        }

        logger.debug("Repository Store URI: {}, file: {}", uri, file.getAbsoluteFile());

        Component store;

        if (!file.exists() && !file.mkdirs()) {
            throw new ConfigurationException(STORE_URI, "Can not create directory on given path: " + path);
        }

        try {
            store = createComponent()
                    .setInterface(Store.class.getName(), null)
                    .setImplementation(new FilebasedStoreImpl(file))
                    .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                    .add(createServiceDependency().setRequired(true).setService(MetadataService.class))
                    .add(createServiceDependency().setRequired(true).setService(ResourceDAO.class))
                    .add(createServiceDependency().setRequired(true).setService(RepositoryDAO.class))
                    .add(createServiceDependency().setRequired(true).setService(MetadataFactory.class))
                    .add(createServiceDependency().setRequired(true).setService(MetadataValidator.class))
                    .add(createServiceDependency().setRequired(true).setService(ResourceLoader.class))
                    .add(createServiceDependency().setRequired(true).setService(ResourceIndexerService.class));
        } catch (IOException e) {
            throw new ConfigurationException(STORE_URI, "Can not create store on given base directory: " + uri, e);
        }

        logger.debug("Registering Repository Store: {}", store);

        dependencyManager.add(store);

        // for more instances of Store (e.g. user would like to choose a store for uploading resources)
        // th ManagedServiceFactory can be used, see:
        // http://www.osgilook.com/2009/08/04/factory-pattern-on-steroids-the-managedservicefactory/
        // http://changelos.com/2010/12/19/using-a-managedservicefactory/

    }

    @Override
    public void destroy(BundleContext bc, DependencyManager dm) throws Exception {
    }
}
