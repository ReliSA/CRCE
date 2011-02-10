package cz.zcu.kiv.crce.plugin.internal;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
        
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        
        final PluginManager pm = new PluginManagerImpl();
        
        manager.add(createComponent()
                .setInterface(PluginManager.class.getName(), null)
                .setImplementation(pm)
                .add(createServiceDependency().setRequired(false).setService(LogService.class))
                .add(createServiceDependency().setRequired(false).setCallbacks("add", "remove").setService(Plugin.class))
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing yet
    }

}
