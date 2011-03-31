package cz.zcu.kiv.crce.webui.internal;

import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionFactory;
import cz.zcu.kiv.crce.repository.Store;

import javax.servlet.http.HttpServletRequest;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public final class Activator extends DependencyActivatorBase {
    
    private static final int RETRY_AFTER = 1000;
    private static final int RETRY_TIMEOUT = 10000;
    
    private static volatile Activator m_instance;

    private volatile BundleContext m_context;           /* injected by dependency manager */
    private volatile PluginManager m_pluginManager;     /* injected by dependency manager */
    private volatile SessionFactory m_sessionFactory;   /* injected by dependency manager */
    private volatile LogService m_log;                  /* injected by dependency manager */
    private volatile Store m_store;                  	/* injected by dependency manager */

    public static Activator instance() {
        return m_instance;
    }

    public PluginManager getPluginManager() {
        return m_pluginManager;
    }
    
    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }
    
    public LogService getLog() {
        return m_log;
    }
    public Store getStore(){
    	return m_store;
    }
    public Buffer getBuffer(HttpServletRequest req) {
        if (req == null) {
            return null;
        }

        String sid = req.getSession(true).getId();
        ServiceReference[] refs;
        
        for (int i = 0; i < RETRY_TIMEOUT; i += RETRY_AFTER) {
            try {
                refs = m_context.getServiceReferences(Buffer.class.getName(), "(" + SessionFactory.SERVICE_SESSION_ID + "=" + sid + ")");
            } catch (InvalidSyntaxException ex) {
                throw new IllegalArgumentException("Unexpected InvalidSyntaxException caused by invalid filter" , ex);
            }

            if (refs != null && refs.length > 0) {
                if (refs.length > 1) {
                    m_log.log(LogService.LOG_WARNING, "Only one instance of Buffer was expected for this session, found: " + refs.length);
                }
                return (Buffer) m_context.getService(refs[0]);
            }
            
            try {
                Thread.sleep(RETRY_AFTER);
            } catch (InterruptedException ex) {
                // nothing
            }
        }
        
        m_log.log(LogService.LOG_ERROR, "No Buffer instance found for this session");
        
        return null;
    }

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        m_instance = this;
        
        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(SessionFactory.class).setRequired(true))
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                .add(createServiceDependency().setService(PluginManager.class).setRequired(true))
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                );

//        final Test t1 = new Test();
//        final Test t2 = new Test();
//
//        manager.add(createComponent().setImplementation(t1).add(createServiceDependency().setService(RepositoryAdmin.class).setRequired(true)));
//        manager.add(createComponent().setImplementation(t2).add(createServiceDependency().setService(RepositoryAdmin.class).setRequired(true)));
//
//        manager.add(createComponent().setImplementation(t1).add(createServiceDependency().setService(BundleStore.class).setRequired(true)));
//        manager.add(createComponent().setImplementation(t2).add(createServiceDependency().setService(BundleStore.class).setRequired(true)));
//
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException ex) {
//                }
//
//                t1.add("file:///U:/repository.xml");
//                t2.add("file:///Q:/DIP/m2repo/repository.xml");
//
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                }
//                t1.print();
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                }
//                t2.print();
//            }
//        }).start();
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do
    }
}
