package cz.zcu.kiv.crce.webui.internal;

import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionFactory;
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
    
    private static volatile Activator m_instance;
    private static volatile BundleContext m_context;

    private volatile PluginManager m_pluginManager;     /* injected by dependency manager */
    private volatile SessionFactory m_sessionFactory;   /* injected by dependency manager */
    private volatile LogService m_log;                  /* injected by dependency manager */

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

    public static Buffer getBuffer(HttpServletRequest req) {
        if (req == null) {
            return null;
        }

        String sid = req.getSession(true).getId();
        ServiceReference[] refs;
        try {
            refs = m_context.getServiceReferences(Buffer.class.getName(), "(" + SessionFactory.SERVICE_SESSION_ID + "=" + sid + ")");
        } catch (InvalidSyntaxException ex) {
            throw new IllegalArgumentException("Unexpected InvalidSyntaxException caused by bad developer", ex);
        }
        
        ServiceReference reference = null;
        if (refs != null && refs.length == 1) {
            reference  = refs[0];
        }
        if (reference != null) {
            return (Buffer) m_context.getService(reference);
        }
        if (refs != null && refs.length > 1) { // XXX log it
            throw new RuntimeException("More than one Buffer services are registered for this session");
        }
        
        return null;
    }

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        m_instance = this;
        m_context = context;
        
        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(SessionFactory.class).setRequired(true))
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                .add(createServiceDependency().setService(PluginManager.class).setRequired(true))
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
