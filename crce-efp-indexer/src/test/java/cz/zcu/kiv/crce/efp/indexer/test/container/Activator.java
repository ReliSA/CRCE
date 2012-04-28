package cz.zcu.kiv.crce.efp.indexer.test.container;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;

/**
 * Activator class for starting testing bundle.
 * Testing bundle is used in testing container.
 */
public class Activator extends DependencyActivatorBase {
	
    /** Logger. */
    private Logger logger = LoggerFactory.getLogger(getClass());


    private static volatile Activator m_instance;
	private volatile MetadataIndexingResultService mEfpIndexer;
	private volatile ResourceCreator resourceCreator;
    
    /** {@inheritDoc} */
    public void init(final BundleContext btx, final DependencyManager manager) throws Exception {

    	logger.debug("================================");
        logger.debug("Activator of testing bundle was called");

        m_instance = this;

        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setRequired(false).setService(MetadataIndexingResultService.class))
                .add(createServiceDependency().setRequired(true).setService(ResourceCreator.class))
                );

		if(resourceCreator == null)
			logger.debug("resourceCreator is null!");
		else
			logger.debug("resourceCreator is not null!");
        
		/*        
		ContainerTestIndexerHandler indexerHandler = new ContainerTestIndexerHandler();
        
		manager.add(createComponent()
				//.setInterface(Plugin.class.getName(), null)
				.setImplementation(indexerHandler)
				.add(createServiceDependency().setRequired(true).setService(PluginManager.class))
				.add(createServiceDependency().setRequired(false).setService(LogService.class))
				.add(createServiceDependency().setRequired(false).setService(EfpIndexerResultService.class))
				.add(createServiceDependency().setRequired(false).setService(ResourceCreator.class))
				);
				*/
   
    }

	@Override
	public void destroy(BundleContext arg0, DependencyManager arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
}
