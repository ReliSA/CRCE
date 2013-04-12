package cz.zcu.kiv.crce.webui.internal;

import javax.servlet.http.HttpServletRequest;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;

import org.osgi.framework.BundleContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;

/**
 * Activator of this bundle
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public final class Activator extends DependencyActivatorBase {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    private static volatile Activator instance;

    private volatile ResourceFactory resourceFactory;
    private volatile ResourceDAO resourceDAO;
    private volatile PluginManager pluginManager;     /* injected by dependency manager */
    private volatile SessionRegister sessionRegister;   /* injected by dependency manager */
    private volatile Store store;                  	/* injected by dependency manager */

    /**
     * MetadataIndexingResultService instance provides by simple way information about metadata indexing process result.
     */
    private volatile MetadataIndexingResultService m_metadataIndexingResult;    /* injected by dependency manager */


    public static Activator instance() {
        if (instance == null) {
            throw new IllegalStateException("Activator instance is null.");
        }
        return instance;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public SessionRegister getSessionRegister() {
        if (sessionRegister == null) {
            throw new IllegalStateException("sessionRegister is null.");
        }
        return sessionRegister;
    }

    public ResourceDAO getResourceDAO() {
        return resourceDAO;
    }

    public ResourceFactory getResourceFactory() {
        return resourceFactory;
    }

    public Store getStore() {
        return store;
    }

    public Buffer getBuffer(HttpServletRequest req) {
        if (req == null) {
            return null;
        }

        String sid = req.getSession(true).getId();
        return sessionRegister.getSessionData(sid).getBuffer();
    }

    /**
     * @return instance of MetadataIndexingResultService provides info about metadata indexing process.
     */
    public MetadataIndexingResultService getMetadataIndexerResult() {
        return m_metadataIndexingResult;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Workaround for providing DM components.")
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        instance = this;

        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(SessionRegister.class).setRequired(true))
                .add(createServiceDependency().setService(PluginManager.class).setRequired(true))
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                .add(createServiceDependency().setService(ResourceFactory.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataIndexingResultService.class).setRequired(false)));


        logger.debug("Webui activator initialized.");
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do
    }
}
