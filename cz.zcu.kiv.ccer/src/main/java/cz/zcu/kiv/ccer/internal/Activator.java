package cz.zcu.kiv.ccer.internal;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;
import org.apache.ace.obr.storage.BundleStore;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 *
 * @author kalwi
 */
public class Activator extends DependencyActivatorBase {

    private volatile ConfigurationAdmin m_config;

    @Override
    public void init(BundleContext bc, DependencyManager dm) throws Exception {

        System.out.println("***** init");

        final Test t = new Test();

        dm.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency()
                    .setService(ConfigurationAdmin.class)
                    .setRequired(true)));

        dm.add(createComponent()
                .setImplementation(t)
                .add(createServiceDependency()
                    .setService(BundleStore.class)
                    .setRequired(true))
                .add(createServiceDependency()
                    .setService(ConfigurationAdmin.class)
                    .setRequired(true)));

        System.out.println("***** config");



        configure("org.apache.ace.obr.storage.file", "fileLocation", "C:\\repo");
//
        System.out.println("***** thread");
//
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                }
//                        System.out.println("m_config == null: " + (m_config == null));
                t.main();

            }
        }).start();
    }

    private void configure(String pid, String... params) throws IOException {
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
}
