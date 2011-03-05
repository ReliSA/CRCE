package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.SessionFactory;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;
import org.apache.ace.obr.storage.BundleStore;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogService;
import org.osgi.service.obr.RepositoryAdmin;

/**
 *
 * @author kalwi
 */
public class Activator extends DependencyActivatorBase {

    private volatile ConfigurationAdmin m_config;   /* injected */
    
    @Override
    public void init(BundleContext bc, DependencyManager dm) throws Exception {

        final Test test = new Test();

        dm.add(createComponent()
                .setInterface(SessionFactory.class.getName(), null)
                .setImplementation(SessionFactoryImpl.class)
                );
        
        Properties d = new Properties();
        d.put("scheme", "http");
        dm.add(createComponent()
                .setInterface(Store.class.getName(), d)
                .setImplementation(ObrStoreImpl.class)
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                );
        
        d = new Properties();
        d.put("scheme", "file");
        dm.add(createComponent()
                .setInterface(Store.class.getName(), d)
                .setImplementation(FilebasedStoreImpl.class)
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                );
        
        dm.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(PriorityActionHandler.class)
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                );
        
        dm.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(ConfigurationAdmin.class).setRequired(true))
                );

        dm.add(createComponent()
                .setImplementation(test)
                .add(createServiceDependency().setService(BundleStore.class).setRequired(true))
                .add(createServiceDependency().setService(RepositoryAdmin.class).setRequired(true))
                .add(createServiceDependency().setService(ConfigurationAdmin.class).setRequired(true))
                );

//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                }
//
//                test.main();
//
//            }
//        }).start();
    }

    @SuppressWarnings("unchecked")
    public void configure(String pid, String... params) throws IOException {
        Configuration conf = m_config.getConfiguration(pid, null);
        Dictionary properties = conf.getProperties();
        
        if (properties == null) {
            properties = new Properties();
        }
        
        boolean changed = false;
        for (int i = 0; i < params.length; i += 2) {
            if (!params[i + 1].equals(properties.get(params[i]))) {
                properties.put(params[i], params[i + 1]);
                changed = true;
            }
        }
        if (changed) {
            conf.update(properties);
        }
    }

    @Override
    public void destroy(BundleContext bc, DependencyManager dm) throws Exception {
    }
}
