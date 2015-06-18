package cz.zcu.kiv.crce.webservices.indexer.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;

import org.osgi.framework.BundleContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
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
        
        // This interface exposes functionality to the other components.
        manager.add(createComponent()
                .setInterface(WebservicesDescription.class.getName(), null)
                .setImplementation(WebservicesDescriptionImpl.class)
                .add(createServiceDependency().setRequired(true).setService(MetadataFactory.class))
                .add(createServiceDependency().setRequired(true).setService(MetadataService.class))
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class)));
        
        // This is just a "dummy" implementation. Plugin interface is implemented only in order for this indexer to appear in a list of used indexers in Web UI,
        // even though this indexer is not in process chain which consist of other indexers.
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(WebservicesDescriptionImpl.class));
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        logger.debug("Destroying Webservices Indexer module.");
    }
}
