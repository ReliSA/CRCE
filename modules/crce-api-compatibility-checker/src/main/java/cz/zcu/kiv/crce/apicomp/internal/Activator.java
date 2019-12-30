package cz.zcu.kiv.crce.apicomp.internal;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityCheckerService;
import cz.zcu.kiv.crce.apicomp.impl.ApiCompatibilityCheckerServiceImpl;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 * Apicomp bundle activator.
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        System.out.println("Starting the Apicomp bundle.");

        manager.add(createComponent()
                .setInterface(ApiCompatibilityCheckerService.class.getName(), null)
                .setImplementation(ApiCompatibilityCheckerServiceImpl.class)
        );
    }
}
