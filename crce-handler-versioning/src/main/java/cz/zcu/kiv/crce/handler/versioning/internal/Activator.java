package cz.zcu.kiv.crce.handler.versioning.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import cz.zcu.kiv.osgi.versionGenerator.service.VersionService;

import cz.zcu.kiv.crce.compatibility.service.CompatibilityService;
import cz.zcu.kiv.crce.concurrency.service.TaskRunnerService;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;

/**
 * Activator of this bundle.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {

        String services[] = {Plugin.class.getName(), ActionHandler.class.getName()};

        manager.add(createComponent()
                .setInterface(services, null)
                .setImplementation(VersioningActionHandler.class)
                .add(createServiceDependency().setRequired(true).setService(VersionService.class))
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                );

        manager.add(createComponent()
                .setInterface(services, null)
                .setImplementation(CompatibilityActionHandler.class)
                .add(createServiceDependency().setRequired(true).setService(TaskRunnerService.class))
                .add(createServiceDependency().setRequired(true).setService(CompatibilityService.class))
                );

        /*manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(IncreaseVersionActionHandler.class)
                );*/
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // do nothing
    }

}
