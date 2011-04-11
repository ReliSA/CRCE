package cz.zcu.kiv.crce.results.internal;

import cz.zcu.kiv.crce.results.ResultsStore;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {
    
    public static final String PID = "cz.zcu.kiv.crce.results";
    
    public static final String STORE_URI = "store.path";

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        
        manager.add(createComponent()
                .setInterface(ResultsStore.class.getName(), null)
                .setImplementation(ResultsStoreImpl.class)
                .add(createConfigurationDependency().setPid(PID))
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        
    }

}
