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

    /**
     * This function is being called upon initialization of this bundle.
     *
     * @param context
     * @param manager
     * @throws Exception
     */
    @Override
    public void init(BundleContext context, final DependencyManager manager) throws Exception {
        logger.debug("Initializing Webservices Indexer module.");
        
        // Plugin interface is implemented only in order for this indexer to appear in a chain of used indexers.
        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(WebserviceDescriptionResourceIndexer.class)
                .add(createServiceDependency().setRequired(true).setService(MetadataFactory.class))
                .add(createServiceDependency().setRequired(true).setService(MetadataService.class))
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class)));
    }

    /**
     * This function is being called upon destruction of this bundle.
     *
     * @param context
     * @param manager
     * @throws Exception
     */
    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        logger.debug("Destroying Webservices Indexer module.");
    }
}
