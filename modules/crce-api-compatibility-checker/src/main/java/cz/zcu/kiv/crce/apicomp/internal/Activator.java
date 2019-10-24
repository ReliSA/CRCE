package cz.zcu.kiv.crce.apicomp.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 * Apicomp bundle activator.
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        // todo: bundle initialization goes here
        System.out.println("Starting the Apicomp bundle.");
    }
}
