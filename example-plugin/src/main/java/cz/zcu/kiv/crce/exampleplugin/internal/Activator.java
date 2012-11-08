package cz.zcu.kiv.crce.exampleplugin.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;


import cz.zcu.kiv.crce.plugin.Plugin;

public final class Activator extends DependencyActivatorBase{
	
	private static volatile Activator m_instance;
	private volatile BundleContext m_context;           /* injected by dependency manager */
	private volatile LogService m_log;                  /* injected by dependency manager */

    public static Activator instance() {
        return m_instance;
    }
    
	public BundleContext getContext() {
		return m_context;
	}

	public LogService getLog() {
		return m_log;
	}
	
	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
		
	}

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		m_instance = this;
		
		manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(ExamplePlugin.class)
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                );
		
	}
	


}
