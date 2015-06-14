package cz.zcu.kiv.crce.repository.maven.internal;

import static cz.zcu.kiv.crce.repository.maven.internal.MavenStoreConfiguration.CFG__REPOSITORY_LOCAL_URI;
import static cz.zcu.kiv.crce.repository.maven.internal.MavenStoreConfiguration.CFG__REPOSITORY_REMOTE_URI;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

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
import cz.zcu.kiv.crce.repository.maven.internal.jmx.RepositoryManagement;
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

        RepositoryConfiguration repositoryConfiguration = null;
        String absolutePath = null;
        switch (configuration.getPrimaryRepository()) {
            case REMOTE:
                repositoryConfiguration = configuration.getRemoteRepository();
                absolutePath = repositoryConfiguration.getUri().getPath();
                logger.debug("URI {} for Remote Maven repository set", repositoryConfiguration.getUri());
                break;

            case LOCAL:
                repositoryConfiguration = configuration.getLocalRepository();
                File mvnStorePath = new File(repositoryConfiguration.getUri());

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
        props.put("name", "Maven: " + repositoryConfiguration.getUri());

        MavenStoreImpl mavenStoreImpl = new MavenStoreImpl(repositoryConfiguration.getUri(), configuration);
        Component storeComponent = createComponent()
                .setInterface(Store.class.getName(), props)
                .setImplementation(mavenStoreImpl)
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
        
        ObjectName objectName = null;
        if (configuration.isJmxEnabled()) {
            RepositoryManagement jmxRepository = new RepositoryManagement();
            jmxRepository.setMavenStoreImpl(mavenStoreImpl);
            jmxRepository.setMavenStoreConfiguration(configuration);
            jmxRepository.setRepositoryConfiguration(repositoryConfiguration);
            
            String mbeanName = PID + ":type=" + repositoryConfiguration.getName();
            try {
                objectName = new ObjectName(mbeanName);
                ManagementFactory.getPlatformMBeanServer().registerMBean(jmxRepository, objectName);
                logger.info("Repository MBean registered: {}", mbeanName);
            } catch (MalformedObjectNameException e) {
                logger.error("Could not register MBean with the given repository name: " + mbeanName);
            } catch (InstanceAlreadyExistsException e) {
                logger.error("MBean with repository name already exists: " + mbeanName, e);
            } catch (MBeanRegistrationException e) {
                logger.error("Could not register MBean", e);
            } catch (NotCompliantMBeanException e) {
                logger.error("Not compliant MBean", e);
            }
        }
        
        ComponentContext componentContext = new ComponentContext(pid, storeComponent, absolutePath, configuration, objectName);
        componentContexts.put(pid, componentContext);
    }

    @Override
    public void deleted(String pid) {
        ComponentContext componentContext = componentContexts.remove(pid);
        if (componentContext != null) {
            logger.debug("Unregistering repository store: {}", componentContext.getComponent());
            
            dependencyManager.remove(componentContext.getComponent());
            
            if (componentContext.getObjectName() != null) {
                try {
                    ManagementFactory.getPlatformMBeanServer().unregisterMBean(componentContext.getObjectName());
                } catch (InstanceNotFoundException e) {
                    logger.warn("Unregistered MBean doesn't exist.", e);
                } catch (MBeanRegistrationException e) {
                    logger.error("Could not unregister MBean.", e);
                }
            }
        }
    }
}
