package cz.zcu.kiv.crce.efp.indexer.internal;

import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CRCE-EFP-Indexer activator class.
 */
public class Activator extends DependencyActivatorBase {

    /** Static instance for access to methods of this class. */
    public static volatile Activator activatorInstance;

    /** MetadataIndexingResultService injected by dependency manager. */
    private volatile MetadataIndexingResultService mMetadataIndexingResult;    /* injected by dependency manager */

    private static Logger m_log = LoggerFactory.getLogger(Activator.class);
    
    @Override
    public final void init(final BundleContext context, final DependencyManager manager) throws Exception {

        activatorInstance = this;


        manager.add(createComponent()
                .setInterface(Plugin.class.getName(), null)
                .setImplementation(ResourceActionHandler.class)
                .add(createServiceDependency().setRequired(true).setService(PluginManager.class))
                .add(createServiceDependency().setService(MetadataIndexingResultService.class).setRequired(false))
                );
    }

    @Override
    public void destroy(final BundleContext context, final DependencyManager manager)
            throws Exception {
        // do nothing
    }


    /**
     * @return activator instance for access to methods of this class.
     */
    public static Activator instance() {
        return activatorInstance;
    }

    /**
     * @return Logger
     */
    public final Logger getLog() {
        return m_log;
    }

    /**
     * @return instance of MetadataIndexingResultService
     * for setting messages about EFP metadata indexing process.
     */
    public final MetadataIndexingResultService getMetadataIndexerResult() {
        return mMetadataIndexingResult;
    }

    /**
     * @param mMetadataIndexingResult the mMetadataIndexingResult to set
     */
    public final void setmMetadataIndexingResult(final MetadataIndexingResultService mMetadataIndexingResult) {
        this.mMetadataIndexingResult = mMetadataIndexingResult;
    }

}
