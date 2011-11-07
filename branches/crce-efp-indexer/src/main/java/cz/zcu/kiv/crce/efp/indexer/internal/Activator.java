package cz.zcu.kiv.crce.efp.indexer.internal;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;


public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
    	
    	for(int i=0;i<5;i++)
    		System.out.println("------------START ACTIVATOR EFP Indexeru ------------");
    	
    	manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(IndexerActionHandler.class)
                .add(createServiceDependency().setRequired(false).setService(LogService.class))
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                
                //.add(createServiceDependency().setRequired(true).setService(org.apache.felix.bundlerepository.RepositoryAdmin.class))
                );
    	
    	/*
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(Trida2.class)
                .add(createServiceDependency().setRequired(false).setService(LogService.class))
                );
        
        
                */
    	
     	
    }
    
    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // do nothing
    }
}
