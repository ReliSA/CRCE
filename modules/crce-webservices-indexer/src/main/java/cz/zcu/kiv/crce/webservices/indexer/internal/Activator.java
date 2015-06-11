package cz.zcu.kiv.crce.webservices.indexer.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;

import org.osgi.framework.BundleContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;

/**
 * Activator of this bundle.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class Activator extends DependencyActivatorBase {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    @Override
    public void init(BundleContext context, final DependencyManager manager) throws Exception {
        logger.debug("Initializing Webservices Indexer module.");
        
        //
        manager.add(createComponent()
                .setInterface(WebservicesDescription.class.getName(), null)
                .setImplementation(WebservicesDescriptionImpl.class)
                .add(createServiceDependency().setRequired(true).setService(MetadataFactory.class))
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class)));
        
        //
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(WebservicesDescriptionImpl.class));
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        logger.debug("Destroying Webservices Indexer module.");
    }
}
