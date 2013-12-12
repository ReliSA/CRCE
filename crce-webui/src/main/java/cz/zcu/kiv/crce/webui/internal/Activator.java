package cz.zcu.kiv.crce.webui.internal;


import javax.servlet.http.HttpServletRequest;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.service.CompatibilitySearchService;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;


/**
 * Activator of this bundle
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public final class Activator extends DependencyActivatorBase {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    private static volatile Activator m_instance;

    private volatile BundleContext m_context;           /* injected by dependency manager */
    private volatile PluginManager m_pluginManager;     /* injected by dependency manager */
    private volatile SessionRegister m_sessionRegister;   /* injected by dependency manager */
    private volatile Store m_store;                  	/* injected by dependency manager */
    private volatile ResourceCreator m_creator;        	/* injected by dependency manager */
    private volatile CompatibilitySearchService m_compatibilityService; /* injected by dependency manager */

    /** MetadataIndexingResultService instance provides by simple way information
     * about metadata indexing process result. */
    private volatile MetadataIndexingResultService m_metadataIndexingResult;    /* injected by dependency manager */

    public static Activator instance() {
        return m_instance;
    }

    public PluginManager getPluginManager() {
        return m_pluginManager;
    }
    
    public SessionRegister getSessionFactory() {
        return m_sessionRegister;
    }
    
    public ResourceCreator getCreator(){
    	return this.m_creator;
    }
    
    public Store getStore(){
    	return m_store;
    }

    public CompatibilitySearchService getCompatibilityService() {
        return m_compatibilityService;
    }

    public Buffer getBuffer(HttpServletRequest req) {
        if (req == null) {
            return null;
        }

        String sid = req.getSession(true).getId();
        return m_sessionRegister.getSessionData(sid).getBuffer();
    }

    /**
     * @return instance of MetadataIndexingResultService provides info about metadata indexing process.
     */
    public MetadataIndexingResultService getMetadataIndexerResult() {
    	return m_metadataIndexingResult;
    }


    
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        m_instance = this;

        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(SessionRegister.class).setRequired(true))
                .add(createServiceDependency().setService(PluginManager.class).setRequired(true))
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                .add(createServiceDependency().setService(ResourceCreator.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataIndexingResultService.class).setRequired(false))
                .add(createServiceDependency().setService(CompatibilitySearchService.class).setRequired(true))
                );
        
       
        logger.debug("Webui activator initialized.");
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do
    }
}
