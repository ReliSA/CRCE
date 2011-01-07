package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.DataModelHelperExt;
import cz.zcu.kiv.crce.metadata.ResourceCreatorFactory;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 *
 * @author kalwi
 */
public class Activator extends DependencyActivatorBase {

    private static volatile DataModelHelperExt m_dataModelHelper = new DataModelHelperExtImpl();
    
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        manager.add(createComponent()
            .setInterface(ResourceCreatorFactory.class.getName(), null)
            .setImplementation(CombinedResourceCreatorFactory.class)
//            .add(createServiceDependency().setService(RepositoryAdmin.class).setRequired(true))
                );
        
//        manager.add(createComponent()
//                .setImplementation(Activator.class)
//                .add(createServiceDependency())
//                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        
    }
    
    public static DataModelHelperExt getHelper() {
        return m_dataModelHelper;
    }
}
