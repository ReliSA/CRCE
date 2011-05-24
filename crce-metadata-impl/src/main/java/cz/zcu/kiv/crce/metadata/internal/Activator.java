package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 * Bundle's activator.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setInterface(ResourceCreator.class.getName(), null)
                .setImplementation(ResourceCreatorImpl.class)
                .add(createServiceDependency().setRequired(true).setService(org.apache.felix.bundlerepository.RepositoryAdmin.class))
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do 
    }

}
