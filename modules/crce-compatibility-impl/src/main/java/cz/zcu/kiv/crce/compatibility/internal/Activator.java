package cz.zcu.kiv.crce.compatibility.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import cz.zcu.kiv.crce.compatibility.CompatibilityFactory;
import cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao;
import cz.zcu.kiv.crce.compatibility.internal.service.CompatibilityServiceImpl;
import cz.zcu.kiv.crce.compatibility.service.CompatibilitySearchService;
import cz.zcu.kiv.crce.compatibility.service.CompatibilityService;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.repository.Store;

/**
 * Date: 17.11.13
 *
 * @author Jakub Danek
 */
public class Activator extends DependencyActivatorBase {

    /**
     * Initialize the dependency manager. Here you can add all components and their dependencies.
     * If something goes wrong and you do not want your bundle to be started, you can throw an
     * exception. This exception will be passed on to the <code>start()</code> method of the
     * bundle activator, causing the bundle not to start.
     *
     * @param context the bundle context
     * @param manager the dependency manager
     * @throws Exception if the initialization fails
     */
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setInterface(CompatibilityFactory.class.getName(), null)
                .setImplementation(CompatibilityFactoryImpl.class));


        String services[] = {CompatibilityService.class.getName(), CompatibilitySearchService.class.getName()};
        manager.add(createComponent()
                .setInterface(services, null)
                .setImplementation(CompatibilityServiceImpl.class)
                .add(createServiceDependency().setService(CompatibilityDao.class).setRequired(true))
                .add(createServiceDependency().setService(CompatibilityFactory.class).setRequired(true))
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataFactory.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataService.class).setRequired(true))
        );
    }

    /**
     * Destroy the dependency manager. Here you can remove all components and their dependencies.
     * Actually, the base class will clean up your dependencies anyway, so most of the time you
     * don't need to do anything here.
     * <p/>
     * If something goes wrong and you do not want your bundle to be stopped, you can throw an
     * exception. This exception will be passed on to the <code>stop()</code> method of the
     * bundle activator, causing the bundle not to stop.
     *
     * @param context the bundle context
     * @param manager the dependency manager
     * @throws Exception if the destruction fails
     */
    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
