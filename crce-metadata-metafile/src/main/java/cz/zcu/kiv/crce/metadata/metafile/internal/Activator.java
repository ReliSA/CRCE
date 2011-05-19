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
 * Activator of this bundle.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    public static final String PID = "cz.zcu.kiv.crce.metadata.metafile.resource";
    
    @Override
    public void init(BundleContext context, final DependencyManager manager) throws Exception {
        
        manager.add(createComponent()
                .setInterface(DataModelHelperExt.class.getName(), null)
                .setImplementation(DataModelHelperExtImpl.class)
                .add(createServiceDependency().setRequired(true).setService(ResourceCreator.class))
                );
        
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(MetafileResourceDAO.class)
                .add(createServiceDependency().setService(ResourceCreator.class).setRequired(true))
                .add(createServiceDependency().setRequired(false).setService(LogService.class))
                .add(createServiceDependency().setService(DataModelHelperExt.class).setRequired(true))
                .add(createConfigurationDependency().setPid(PID))
                );
        
        MetafileRepositoryDAO repositoryDAO = new MetafileRepositoryDAO();
                
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(repositoryDAO)
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                .add(createServiceDependency().setRequired(true).setService(ResourceCreator.class))
                .add(createServiceDependency().setRequired(false).setService(LogService.class))
                .add(createServiceDependency().setRequired(false).setService(DataModelHelperExt.class))
                );
        
        manager.add(createComponent()
                .setInterface(MetadataGenerator.class.getName(), null)
                .setImplementation(repositoryDAO)
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        
    }

}
