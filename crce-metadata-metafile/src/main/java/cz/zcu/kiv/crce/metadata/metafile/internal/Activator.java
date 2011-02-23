package cz.zcu.kiv.crce.metadata.metafile.internal;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.metafile.DataModelHelperExt;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import org.apache.ace.obr.metadata.MetadataGenerator;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 *
 * @author kalwi
 */
public class Activator extends DependencyActivatorBase {

    private static DataModelHelperExt m_dataModelHelper = new DataModelHelperExtImpl();
    
    @Override
    public void init(BundleContext context, final DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(MetafileResourceDAO.class)
                .add(createServiceDependency().setService(ResourceCreator.class).setRequired(true))
                );
        
        MetafileRepositoryDAO repositoryDAO = new MetafileRepositoryDAO();
                
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(repositoryDAO)
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                .add(createServiceDependency().setRequired(true).setService(ResourceCreator.class))
                .add(createServiceDependency().setRequired(false).setService(LogService.class))
                );
        
        manager.add(createComponent()
                .setInterface(MetadataGenerator.class.getName(), null)
                .setImplementation(repositoryDAO)
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        
    }
    
    public static DataModelHelperExt getHelper() {
        return m_dataModelHelper;
    }
}
