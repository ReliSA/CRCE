package cz.zcu.kiv.crce.compatibility.dao.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedService;

import cz.zcu.kiv.crce.compatibility.CompatibilityFactory;
import cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao;

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
        //set service PID
        Dictionary<String, Object> compatibilityProps = new Hashtable(1);
        compatibilityProps.put("service.pid","cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao");

        //Register as both CompatibilityDao and ManagedService
        String s[] = {CompatibilityDao.class.getName(), ManagedService.class.getName()};
        manager.add(createComponent()
                .setInterface(s, compatibilityProps)
                .setFactory(new CompatibilityDaoMongoFactory(), "get")
                //requires CompatibilityFactory for proper mapping
                .add(createServiceDependency().setRequired(true)
                        .setService(CompatibilityFactory.class))
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
        DbContext.stop(); //close existing mongo connections
    }
}
