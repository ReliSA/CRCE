package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.SessionFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Dictionary;
import java.util.Properties;
import org.apache.ace.obr.storage.BundleStore;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.osgi.service.obr.RepositoryAdmin;

/**
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase implements ManagedService {
    
    public static final String PID = "cz.zcu.kiv.crce.repository";

    private static volatile Activator m_instance;   /* injected by dependency manager */
    private static volatile BundleContext m_context;       /* injected by dependency manager */
    
    private Store m_store = null;
    
    /**
     * Returns an instance of this class.
     * @return 
     */
    public static Activator instance() {
        return m_instance;
    }

    public Store getStore() {
        return m_store;
    }
    
    @Override
    public void init(BundleContext bc, DependencyManager dm) throws Exception {
        m_instance = this;
        m_context = bc;
        
        dm.add(createComponent()
                .setImplementation(this)
                .add(createConfigurationDependency().setPid(PID))
                );

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
                .add(createServiceDependency().setRequired(false).setService(LogService.class))
                );

        dm.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(DefaultResourceDAOFactory.class)
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                );
        
        // XXX vvv - only for testing purposes
        
        final Test test = new Test();
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

    @Override
    public void updated(Dictionary dict) throws ConfigurationException {
        if (dict == null) {
            return;
        }
        URI uri;
        try {
            uri = new URI((String) dict.get("store.uri"));
        } catch (URISyntaxException ex) {
            throw new ConfigurationException("store.uri", "Invalid URI: " + dict.get("uri"), ex);
        }
        
        ServiceReference[] refs;
        try {
            refs = m_context.getServiceReferences(Store.class.getName(), "(" + "scheme" + "=" + uri.getScheme() + ")");
        } catch (InvalidSyntaxException ex) {
            throw new IllegalArgumentException("Unexpected InvalidSyntaxException caused by invalid filter", ex);
        }
        
        if (refs != null && refs.length > 0) {
            m_store = (Store) m_context.getService(refs[0]);
        } else {
            throw new ConfigurationException("store.uri", "No registered Store service for given uri: " + uri);
        }
    }

//    @SuppressWarnings("unchecked")
//    public void configure(String pid, String... params) throws IOException {
//        Configuration conf = m_config.getConfiguration(pid, null);
//        Dictionary properties = conf.getProperties();
//        
//        if (properties == null) {
//            properties = new Properties();
//        }
//        
//        boolean changed = false;
//        for (int i = 0; i < params.length; i += 2) {
//            if (!params[i + 1].equals(properties.get(params[i]))) {
//                properties.put(params[i], params[i + 1]);
//                changed = true;
//            }
//        }
//        if (changed) {
//            conf.update(properties);
//        }
//    }

    @Override
    public void destroy(BundleContext bc, DependencyManager dm) throws Exception {
    }
}
