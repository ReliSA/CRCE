package cz.zcu.kiv.crce.metadata.metafile.internal;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.metafile.DataModelHelperExt;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import org.apache.ace.obr.metadata.MetadataGenerator;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 * Activator of this bundle.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

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
                .add(createServiceDependency().setService(DataModelHelperExt.class).setRequired(true))
                );
        
        manager.add(createComponent()	
                .setInterface(ResourceDAO.class.getName(), null)	// for crce-efp-indexer testing container
                .setImplementation(MetafileResourceDAO.class)
                .add(createServiceDependency().setService(ResourceCreator.class).setRequired(true))
                .add(createServiceDependency().setService(DataModelHelperExt.class).setRequired(true))
                );
        
        MetafileRepositoryDAO repositoryDAO = new MetafileRepositoryDAO();
                
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(repositoryDAO)
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                .add(createServiceDependency().setRequired(true).setService(ResourceCreator.class))
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
