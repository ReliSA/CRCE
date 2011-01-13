package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.ResourceCreatorFactory;
import cz.zcu.kiv.crce.repository.Repository;
import cz.zcu.kiv.crce.repository.ResourceBuffer;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;
import org.apache.ace.obr.metadata.MetadataGenerator;
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
    private static volatile MetadataGenerator m_metadataGenerator; /* injected */
    
    private volatile RepositoryAdmin m_repositoryAdmin; /* injected */

    @Override
    public void init(BundleContext bc, DependencyManager dm) throws Exception {

        final Test test = new Test();

        dm.add(createComponent()
                .setInterface(ResourceBuffer.class.getName(), null)
                .setImplementation(ResourceBufferImpl.class)
                .add(createServiceDependency().setService(ResourceCreatorFactory.class).setRequired(true)));

        dm.add(createComponent()
                .setInterface(Repository.class.getName(), null)
                .setImplementation(RepositoryImpl.class)
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                );
        
        dm.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(ConfigurationAdmin.class).setRequired(true)));

        dm.add(createComponent()
                .setImplementation(test)
                .add(createServiceDependency().setService(BundleStore.class).setRequired(true))
                .add(createServiceDependency().setService(RepositoryAdmin.class).setRequired(true))
                .add(createServiceDependency().setService(ConfigurationAdmin.class).setRequired(true))
                );



        configure("org.apache.ace.obr.storage.file", "fileLocation", "U:");
        configure("org.apache.ace.obr.servlet", "org.apache.ace.server.servlet.endpoint", "/obr");
//        configure("cz.zcu.kiv.crce.webui.upload", "org.apache.ace.server.servlet.endpoint", "/upload");

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
        System.out.println("props=" + properties);
        boolean changed = false;
        for (int i = 0; i < params.length; i += 2) {
            System.out.println("key=" + params[i] + " value=" + params[i + 1]);
            if (!params[i + 1].equals(properties.get(params[i]))) {
                properties.put(params[i], params[i + 1]);
                changed = true;
            }
        }
        if (changed) {
            System.out.println("Updating " + pid);
            conf.update(properties);
        }
    }

    @Override
    public void destroy(BundleContext bc, DependencyManager dm) throws Exception {
    }
    
    public static MetadataGenerator getMetadataGenerator() {
        return m_metadataGenerator;
    }
}