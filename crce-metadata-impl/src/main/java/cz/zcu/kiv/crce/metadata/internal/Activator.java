package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.ResourceFactory;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 * Bundle's activator.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {

        manager.add(createComponent()
                .setInterface(ResourceFactory.class.getName(), null)
                .setImplementation(ResourceFactoryImpl.class));
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do
    }

}
