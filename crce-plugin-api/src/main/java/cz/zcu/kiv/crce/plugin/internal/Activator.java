package cz.zcu.kiv.crce.plugin.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 *
 * @author kalwi
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        System.out.println("Plugin API init");
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing yet
    }

}
