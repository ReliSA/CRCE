package cz.zcu.kiv.crce.crce_integration_tests.rest;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.Store;

public final class Activator extends DependencyActivatorBase {

	private static volatile Activator m_instance;
	private volatile Store m_store;                  	/* injected by dependency manager */
	private volatile Buffer m_buffer;   				/* injected by dependency manager */
	
    public static Activator instance() {
        return m_instance;
    }
    
	public Buffer getBuffer() {
		return m_buffer;
	}
    public Store getStore(){
    	return m_store;
    }

	@Override
	public void destroy(BundleContext arg0, DependencyManager arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(BundleContext arg0, DependencyManager manager)
			throws Exception {
		
		m_instance = this;
		
		manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                .add(createServiceDependency().setService(Buffer.class).setRequired(true))
                );

	}

}
