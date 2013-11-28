package cz.zcu.kiv.crce.handler.metrics.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;


public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
        
		manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(FileSizeHandler.class)
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                );
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
		// do nothing		
	}
}
