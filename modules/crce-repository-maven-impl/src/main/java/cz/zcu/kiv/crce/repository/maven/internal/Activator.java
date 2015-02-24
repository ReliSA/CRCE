package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.concurrency.service.TaskRunnerService;
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
 * @author MBr
 */
public class Activator extends DependencyActivatorBase implements ManagedServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    public static final String PID = "cz.zcu.kiv.crce.repository.maven";

    public static final String STORE_URI = "store.uri";

    private final Map<String, Component> components = new HashMap<>();

    private volatile DependencyManager dependencyManager; /* injected by dependency manager */


    @Override
    public void init(BundleContext bc, DependencyManager dm) throws Exception {
    	logger.debug("Maven repo activator init");
        Properties props = new Properties();
        props.put(Constants.SERVICE_PID, PID);
        dm.add(createComponent()
                .setInterface(ManagedServiceFactory.class.getName(), props)
                .setImplementation(this)                
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
    public void destroy(BundleContext bc, DependencyManager dm) throws Exception {
    }

    @Override
    public String getName() {
        return "Maven repository store factory.";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        logger.debug("Updating Maven repository ({}) configuration: {}", properties);

        if (components.containsKey(pid)) {
            logger.info("Repository ({}) is already configured.", pid);
            return;
        }

        if (properties == null) {
            logger.warn("Repository ({}) configuration is empty!", pid);
            return;
        }

        String path = (String) properties.get(STORE_URI);

        URI uri = null;
        File file = null;
        try {
            uri = new URI(path);
            if (uri.getScheme() == null) {
                file = new File(path);
                uri = file.toURI();
            } else if ("file".equals(uri.getScheme())) {
                file = new File(uri);
            } else {
                throw new ConfigurationException(STORE_URI, "No Store implementation for given URI scheme: " + uri.getScheme());
            }
        } catch (URISyntaxException ex) {
            // TODO verify this usecase and correctness
            file = new File(path);
            uri = file.toURI();
        }

        logger.debug("Repository Store URI: {}, file: {}", uri, file.getAbsoluteFile());

        Component mavenStore = createComponent()
                .setInterface(Store.class.getName(), null)
                .setImplementation(new MavenStoreImpl(uri))
                .add(dependencyManager.createConfigurationDependency().setPid(pid))
                    .add(createServiceDependency().setRequired(true).setService(MetadataFactory.class))
                    .add(createServiceDependency().setRequired(true).setService(ResourceDAO.class))
                    .add(createServiceDependency().setRequired(true).setService(RepositoryDAO.class))
                    .add(createServiceDependency().setRequired(true).setService(ResourceLoader.class))
                    .add(createServiceDependency().setRequired(true).setService(IdentityIndexer.class))
                    .add(createServiceDependency().setRequired(true).setService(TaskRunnerService.class))
                    .add(createServiceDependency().setRequired(true).setService(ResourceIndexerService.class))
                    .add(createServiceDependency().setRequired(true).setService(MetadataService.class))
                    .add(createServiceDependency().setRequired(true).setService(IdentityIndexer.class))
                    .add(createServiceDependency().setRequired(true).setService(MetadataValidator.class))
                ;

        logger.debug("Registering Repository Store: {}", mavenStore);

        components.put(pid, mavenStore);
        dependencyManager.add(mavenStore);
    }

    @Override
    public void deleted(String pid) {
        dependencyManager.remove(components.remove(pid));
    }

}
