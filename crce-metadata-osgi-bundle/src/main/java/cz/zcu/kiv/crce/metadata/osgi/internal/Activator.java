package cz.zcu.kiv.crce.metadata.osgi.internal;

import cz.zcu.kiv.crce.plugin.Plugin;
import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(OsgiManifestBundleIndexer.class)
                .add(createServiceDependency().setService(RepositoryAdmin.class).setRequired(true))
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        
    }
    
}
