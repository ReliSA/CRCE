package cz.zcu.kiv.crce.metadata.file.internal;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 *
 * @author kalwi
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, final DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(FileIndexingResourceDAO.class)
                .add(createServiceDependency().setService(PluginManager.class).setRequired(true))
                );
        
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(FileTypeResourceIndexer.class)
                .add(createServiceDependency().setService(ResourceCreator.class).setRequired(true))
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        
    }
    
}
