package cz.zcu.kiv.crce.handler.metrics.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.plugin.Plugin;



public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
        
		manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(MetricsIndexer.class)
                .add(createServiceDependency().setService(ResourceFactory.class).setRequired(true))
                .add(createServiceDependency().setRequired(true).setService(MetadataService.class))
                );
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
		// do nothing		
	}
}
