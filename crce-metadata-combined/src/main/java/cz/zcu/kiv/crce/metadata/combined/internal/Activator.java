package cz.zcu.kiv.crce.metadata.combined.internal;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    private static volatile Activator m_instance;
    
    private volatile ResourceCreator m_resourceCreator; /* injected by dependency manager */

    public static Activator instance() {
        return m_instance;
    }
    
    public ResourceCreator getResourceCreator() {
        return m_resourceCreator;
    }
    
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        m_instance = this;
        
        manager.add(createComponent()
                .setImplementation(this) // TODO this or class?
                .add(createServiceDependency().setService(ResourceCreator.class).setRequired(true))
                );
        
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(CombinedResourceDAOFactory.class)
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                );
        
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do
    }
}
