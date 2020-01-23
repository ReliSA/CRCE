package cz.zcu.kiv.crce.apicomp.internal;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityCheckerService;
import cz.zcu.kiv.crce.apicomp.impl.ApiCompatibilityCheckerServiceImpl;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

/**
 * Apicomp bundle activator.
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);

        System.out.println("Start the Apicomp bundle.");
    }

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        System.out.println("Init the Apicomp bundle.");


        context.registerService(ApiCompatibilityCheckerService.class, new ApiCompatibilityCheckerServiceImpl(), new Hashtable<>());
//        manager.add(createComponent()
//                .setInterface(ApiCompatibilityCheckerService.class.getName(), null)
//                .setImplementation(ApiCompatibilityCheckerServiceImpl.class)
//        );
    }
}
