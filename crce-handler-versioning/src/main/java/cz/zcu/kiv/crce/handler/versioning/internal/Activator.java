package cz.zcu.kiv.crce.handler.versioning.internal;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.osgi.versionGenerator.service.VersionService;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 * Activator of this bundle.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(VersioningActionHandler.class)
                .add(createServiceDependency().setRequired(true).setService(VersionService.class))
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                );
        
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(IncreaseVersionActionHandler.class)
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // do nothing
    }

}
