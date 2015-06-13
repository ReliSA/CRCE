package cz.zcu.kiv.crce.repository.maven.internal;

import static cz.zcu.kiv.crce.repository.maven.internal.MavenStoreConfiguration.CFG__REPOSITORY_LOCAL_URI;
import static cz.zcu.kiv.crce.repository.maven.internal.MavenStoreConfiguration.CFG__REPOSITORY_REMOTE_URI;

import java.io.File;
import java.net.URI;
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
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.resolver.ResourceLoader;

/**
 * Activator of this bundle.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @author Miroslav Brozek
 */
public class Activator extends DependencyActivatorBase implements ManagedServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    public static final String PID = "cz.zcu.kiv.crce.repository.maven";

    /**
     * PID to ComponentContext.
     */
    private final Map<String, ComponentContext> componentContexts = new HashMap<>();

    private volatile DependencyManager dependencyManager; /* injected by dependency manager */

    @Override
    public void init(BundleContext bc, DependencyManager dm) throws Exception {
        logger.debug("Maven repository activator - initializing");

        Properties props = new Properties();
        props.put(Constants.SERVICE_PID, PID);
        dm.add(createComponent()
                .setInterface(ManagedServiceFactory.class.getName(), props)
                .setImplementation(this)
                );
    }

    @Override
    public void destroy(BundleContext bc, DependencyManager dm) throws Exception {
        for (ComponentContext componentContext : componentContexts.values()) {
            dependencyManager.remove(componentContext.getComponent());
        }

        logger.debug("Maven repository destroyed.");
    }

    @Override
    public String getName() {
        return "Maven repository store factory.";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        logger.trace("ManagedServiceFactory updated with pid: {}", pid);

        if (properties == null) {
            logger.warn("Repository ({}) configuration is empty!", pid);
            return;
        }

        logger.debug("Updating maven repository ({}) configuration: {}", properties);

        MavenStoreConfiguration configuration = new MavenStoreConfiguration(pid, properties);
        if (!configuration.isEnabled()) {
            logger.debug("Maven repository disabled, PID: {}", pid);
            
            deleted(pid);
            
            return;
        }

        String absolutePath = null;
        URI uri = null;
        switch (configuration.getPrimaryRepository()) {
            case REMOTE:
                uri = configuration.getRemoteRepository().getUri();
                absolutePath = uri.getPath();
                logger.debug("URI {} for Remote Maven repository set", uri);
                break;

            case LOCAL:
                uri = configuration.getLocalRepository().getUri();
                File mvnStorePath = new File(uri);

                absolutePath = mvnStorePath.getAbsolutePath();

                if (!mvnStorePath.exists() && !mvnStorePath.mkdirs()) {
                    throw new ConfigurationException(CFG__REPOSITORY_LOCAL_URI, "Can not create directory on the given path: " + absolutePath);
                } else if (!mvnStorePath.isDirectory()) {
                    throw new ConfigurationException(CFG__REPOSITORY_LOCAL_URI, "Store URI is not a directory: " + absolutePath);
                }
                break;
        }

        for (Map.Entry<String, ComponentContext> entry : componentContexts.entrySet()) {
            if (entry.getValue().getAbsoluteUri().equals(absolutePath) && !entry.getKey().equals(pid)) {
                throw new ConfigurationException(
                        RepositoryType.REMOTE.equals(configuration.getPrimaryRepository()) ? CFG__REPOSITORY_REMOTE_URI : CFG__REPOSITORY_LOCAL_URI,
                        "Another repository (PID: " + entry.getKey() + ") is already configured for this path: " + absolutePath
                );
            }
        }

        ComponentContext oldComponentContext = componentContexts.get(pid);
        if (oldComponentContext != null) {
            if (oldComponentContext.getAbsoluteUri().equals(absolutePath)) {
                logger.debug("Repository (PID: {}) is already configured for this path: {}", pid, absolutePath);
                return;
            } else {
                deleted(pid);
            }
        }
        
        Properties props = new Properties();
        props.put("id", pid);
        props.put("name", "Maven: " + uri);

        Component storeComponent = createComponent()
                .setInterface(Store.class.getName(), props)
                .setImplementation(new MavenStoreImpl(uri, configuration))
//                .add(dependencyManager.createConfigurationDependency().setPid(pid).setPropagate(true))
                .add(createServiceDependency().setRequired(true).setService(MetadataService.class))
                .add(createServiceDependency().setRequired(true).setService(ResourceDAO.class))
                .add(createServiceDependency().setRequired(true).setService(RepositoryDAO.class))
                .add(createServiceDependency().setRequired(true).setService(MetadataFactory.class))
                .add(createServiceDependency().setRequired(true).setService(MetadataValidator.class))
                .add(createServiceDependency().setRequired(true).setService(ResourceLoader.class))
                .add(createServiceDependency().setRequired(true).setService(IdentityIndexer.class))
                .add(createServiceDependency().setRequired(true).setService(ResourceIndexerService.class))
                .add(createServiceDependency().setRequired(true).setService(TaskRunnerService.class))
                .add(createServiceDependency().setRequired(true).setService(IdentityIndexer.class));

        logger.debug("Registering maven repository store: {}", storeComponent);

        dependencyManager.add(storeComponent);
        
        ComponentContext componentContext = new ComponentContext(pid, storeComponent, absolutePath, configuration);
        componentContexts.put(pid, componentContext);
    }

    @Override
    public void deleted(String pid) {
        ComponentContext componentContext = componentContexts.remove(pid);
        if (componentContext != null) {
            logger.debug("Unregistering repository store: {}", componentContext.getComponent());
            
            dependencyManager.remove(componentContext.getComponent());
        }
    }
}
