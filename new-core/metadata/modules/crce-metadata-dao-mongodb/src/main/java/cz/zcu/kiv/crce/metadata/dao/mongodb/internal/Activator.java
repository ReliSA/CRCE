package cz.zcu.kiv.crce.metadata.dao.mongodb.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import cz.zcu.kiv.crce.metadata.dao.mongodb.DbContext;


/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    public static final String PID = "cz.zcu.kiv.crce.metadata.dao";

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
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