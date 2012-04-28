package cz.zcu.kiv.crce.webui.internal;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;

import javax.servlet.http.HttpServletRequest;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 * Activator of this bundle
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public final class Activator extends DependencyActivatorBase {

    private static volatile Activator m_instance;

    private volatile BundleContext m_context;           /* injected by dependency manager */
    private volatile PluginManager m_pluginManager;     /* injected by dependency manager */
    private volatile SessionRegister m_sessionRegister;   /* injected by dependency manager */
    private volatile LogService m_log;                  /* injected by dependency manager */
    private volatile Store m_store;                  	/* injected by dependency manager */
    private volatile ResourceCreator m_creator;        	/* injected by dependency manager */

    /** MetadataIndexingResultService instance provides by simple way information
     * about result of EFP indexing process in crce-efp-indexer module. */
    private volatile MetadataIndexingResultService m_efpIndexerResult;    /* injected by dependency manager */

    public static Activator instance() {
        return m_instance;
    }

    public PluginManager getPluginManager() {
        return m_pluginManager;
    }
    
    public SessionRegister getSessionFactory() {
        return m_sessionRegister;
    }
    
    public ResourceCreator getCreator(){
    	return this.m_creator;
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
        return m_sessionRegister.getSessionData(sid).getBuffer();
    }

    /**
     * @return instance of MetadataIndexingResultService provides info about EFP indexing process.
     */
    public MetadataIndexingResultService getEfpIndexerResult() {
    	return m_efpIndexerResult;
    }

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        m_instance = this;

        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(SessionRegister.class).setRequired(true))
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                .add(createServiceDependency().setService(PluginManager.class).setRequired(true))
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                .add(createServiceDependency().setService(ResourceCreator.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataIndexingResultService.class).setRequired(false))
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
