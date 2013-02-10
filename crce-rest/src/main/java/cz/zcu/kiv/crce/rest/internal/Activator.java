package cz.zcu.kiv.crce.rest.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;


import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Store;

public final class Activator extends DependencyActivatorBase{
	
	private static volatile Activator m_instance;
	private volatile BundleContext m_context;           /* injected by dependency manager */
	private volatile LogService m_log;                  /* injected by dependency manager */
    private volatile Store m_store;                  	/* injected by dependency manager */

    public static Activator instance() {
        return m_instance;
    }
    
	public BundleContext getContext() {
		return m_context;
	}

	public LogService getLog() {
		return m_log;
	}
    public Store getStore(){
    	return m_store;
    }
	
	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
		
	}

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		m_instance = this;
		
		manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                );
		
	}
	


}
