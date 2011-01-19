package cz.zcu.kiv.crce.metadata.combined.internal;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.plugin.ResourceDAOFactory;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 *
 * @author kalwi
 */
public class Activator extends DependencyActivatorBase {

    private static volatile ResourceCreator m_resourceCreator;
    
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setImplementation(this) // TODO this or class?
                .add(createServiceDependency().setService(ResourceCreator.class).setRequired(true))
                );
        
        manager.add(createComponent()
                .setInterface(ResourceDAOFactory.class.getName(), null)
                .setImplementation(CombinedResourceDAOFactory.class)
                );
        
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do
    }
    
    public static ResourceCreator getResourceCreator() {
        return m_resourceCreator;
    }
    
}
