package cz.zcu.kiv.crce.plugin.internal;

import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
        
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    static final PluginManagerImpl pm = new PluginManagerImpl();
    
    /** This instance of MetadataIndexingResultService implementation provides by simple way information
     * about result of EFP indexing process in crce-efp-indexer module. */
    private MetadataIndexingResultService efpIndexerResult = new MetadataIndexingResultServiceImpl();
    
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        
        manager.add(createComponent()
                .setInterface(PluginManager.class.getName(), null)
                .setImplementation(pm)
                .add(createServiceDependency().setRequired(true).setService(EventAdmin.class))
                .add(createServiceDependency().setRequired(false).setCallbacks("register", "unregister").setService(Plugin.class))
                );
        
		manager.add(createComponent()
				.setInterface(MetadataIndexingResultService.class.getName(), null)
				.setImplementation(efpIndexerResult)
				);
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing yet
    }
    
}
