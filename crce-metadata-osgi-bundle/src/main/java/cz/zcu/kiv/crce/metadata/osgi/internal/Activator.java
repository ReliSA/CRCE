package cz.zcu.kiv.crce.metadata.osgi.internal;

import cz.zcu.kiv.crce.metadata.osgi.DataModelHelperExt;
import cz.zcu.kiv.crce.plugin.ResourceDAO;
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
                .setImplementation(OsgiManifestResourceDAO.class)
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        
    }
    
    public static DataModelHelperExt getHelper() {
        return m_dataModelHelper;
    }
}
