package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.IOException;
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
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.resolver.ResourceLoader;

/**
 * Activator of this bundle.
 * @author Miroslav Brozek
 */
public class Activator extends DependencyActivatorBase implements ManagedServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    public static final String PID = "cz.zcu.kiv.crce.repository.maven";

    private final Map<String, Component> components = new HashMap<>();

    private volatile DependencyManager dependencyManager; /* injected by dependency manager */


    @Override
    public void init(BundleContext bc, DependencyManager dm) throws Exception {
    	logger.debug("Maven repository activator init method started");
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
        
      
        logger.debug("Maven repository activator init method ended");
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
        
        MavenStoreConfig.initConfig(properties);
        
        URI uri;
    	if (MavenStoreConfig.isRemoteRepoDefault()) {
    		uri = MavenStoreConfig.getRemoteRepository().getUri();
    	}
    	else{
    		uri = MavenStoreConfig.getLocalRepository().getUri();
    	}
    	

        Component mavenStore;
		try {
			mavenStore = createComponent()
			        .setInterface(Store.class.getName(), null)
			        .setImplementation(new MavenStoreImpl(uri, MavenStoreConfig.isRemoteRepoDefault()))
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
			            .add(createServiceDependency().setRequired(true).setService(MetadataValidator.class));
		} catch (IOException e) {
			 throw new ConfigurationException(PID, "Wrong URI syntax, check Configuration file: " + uri, e);
		}

        logger.debug("Registering Repository Maven Store: {}", mavenStore);

        components.put(pid, mavenStore);
        dependencyManager.add(mavenStore);
    }


	@Override
    public void deleted(String pid) {
        dependencyManager.remove(components.remove(pid));
    }

}
