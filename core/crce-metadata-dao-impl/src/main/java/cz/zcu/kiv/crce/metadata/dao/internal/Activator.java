package cz.zcu.kiv.crce.metadata.dao.internal;

import org.osgi.framework.BundleContext;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;



/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    public static final String PID = "cz.zcu.kiv.crce.metadata.dao";

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        // TODO logging
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // TODO logging
    }
}
