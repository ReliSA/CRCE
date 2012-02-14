package cz.zcu.kiv.crce.efp.indexer.internal;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 * CRCE-EFP-Indexer activator class.
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
    	
    	// In indexer is used PluginManager and LogService dependency injection.
    	manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(IndexerActionHandler.class)
                .add(createServiceDependency().setRequired(false).setService(LogService.class))
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                );
     }
    
    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // do nothing
    }
}
