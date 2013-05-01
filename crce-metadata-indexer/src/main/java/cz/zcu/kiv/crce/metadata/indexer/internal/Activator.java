package cz.zcu.kiv.crce.metadata.indexer.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexer;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexerService;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;

/**
 * Activator of this bundle.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, final DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setInterface(ResourceIndexerService.class.getName(), null)
                .setImplementation(ResourceIndexerServiceImpl.class)
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                .add(createServiceDependency().setRequired(true).setService(ResourceFactory.class))
                .add(createServiceDependency().setRequired(false).setCallbacks("add", "remove").setService(ResourceIndexer.class))
                );

        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(FileTypeResourceIndexer.class)
                .add(createServiceDependency().setRequired(true).setService(ResourceFactory.class))
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {

    }

}
