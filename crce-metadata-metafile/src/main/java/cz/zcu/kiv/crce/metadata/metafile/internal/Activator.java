package cz.zcu.kiv.crce.metadata.metafile.internal;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.ResourceDAO;
import cz.zcu.kiv.crce.metadata.metafile.DataModelHelperExt;
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
                .setInterface(ResourceDAO.class.getName(), null)
                .setImplementation(MetafileResourceDAO.class)
                .add(createServiceDependency().setService(ResourceCreator.class).setRequired(true))
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        
    }
    
    public static DataModelHelperExt getHelper() {
        return m_dataModelHelper;
    }
}
