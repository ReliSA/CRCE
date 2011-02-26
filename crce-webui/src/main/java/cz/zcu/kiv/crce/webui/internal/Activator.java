package cz.zcu.kiv.crce.webui.internal;

import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import org.apache.ace.obr.storage.BundleStore;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.service.obr.RepositoryAdmin;

/**
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public final class Activator extends DependencyActivatorBase {

    private static volatile Buffer m_stack; /* injected */

    private static volatile LogService m_log; /* injected */

    private static volatile PluginManager m_pluginManager; /* injected */

    public static PluginManager getPluginManager() {
        return m_pluginManager;
    }
    
    public static Buffer getBuffer() {
        return m_stack;
    }

    public static LogService getLog() {
        return m_log;
    }

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(Buffer.class).setRequired(true))
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
