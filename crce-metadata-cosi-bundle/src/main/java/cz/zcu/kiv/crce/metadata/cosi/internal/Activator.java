package cz.zcu.kiv.crce.metadata.cosi.internal;

import cz.zcu.kiv.crce.plugin.Plugin;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 * Activator of this bundle.
 * @author Natalia Rubinova
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(CoSiManifestBundleIndexer.class)
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        
    }
    
}
