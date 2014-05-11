package cz.zcu.kiv.crce.plugin.internal;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    static final PluginManagerImpl pluginManager = new PluginManagerImpl();

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {

        manager.add(createComponent()
                .setInterface(PluginManager.class.getName(), null)
                .setImplementation(pluginManager)
                .add(createServiceDependency().setRequired(true).setService(EventAdmin.class))
                .add(createServiceDependency().setRequired(false).setCallbacks("register", "unregister").setService(Plugin.class))
                );

    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing yet
    }

}
