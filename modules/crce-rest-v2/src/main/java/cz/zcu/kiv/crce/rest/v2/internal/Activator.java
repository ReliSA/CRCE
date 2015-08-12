package cz.zcu.kiv.crce.rest.v2.internal;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.service.CompatibilitySearchService;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.osgi.util.FilterParser;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.vo.service.MappingService;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "UWF_UNWRITTEN_FIELD", justification = "Injected by dependency manager.")
public final class Activator extends DependencyActivatorBase {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    private static volatile Activator instance;

    /*
     * Injected by Dependency Manager.
     */
    private volatile Store store;
    private volatile MetadataService metadataService;
    private volatile MetadataFactory metadataFactory;
    private volatile FilterParser filterParser;
    private volatile CompatibilitySearchService compatibilityService;
    private volatile SessionRegister sessionRegister;
    private volatile MappingService mappingService;

    public static Activator instance() {
        return instance;
    }

    public Store getStore() {
        return store;
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

    public MetadataFactory getMetadataFactory() {
        return metadataFactory;
    }

    public FilterParser getFilterParser() {
        return filterParser;
    }

    public MappingService getMappingService() {
        return mappingService;
    }

    @Nullable
    public CompatibilitySearchService getCompatibilityService() {
        if(compatibilityService == null) {
            logger.info("Compatibility service is not available!");
        }
        return compatibilityService;
    }

    public Buffer getBuffer(HttpServletRequest req) {
        if (req == null) {
            return null;
        }

        String sid = req.getSession(true).getId();
        return sessionRegister.getSessionData(sid).getBuffer();
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {

    }

    @Override
    @edu.umd.cs.findbugs.annotations
            .SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Dependency manager workaround.")
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        instance = this;

        logger.debug("Initializing Rest-V2 Activator!");

        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataService.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataFactory.class).setRequired(true))
                .add(createServiceDependency().setService(FilterParser.class).setRequired(true))
                .add(createServiceDependency().setService(SessionRegister.class).setRequired(true))
                .add(createServiceDependency().setService(MappingService.class).setRequired(true))
                .add(createServiceDependency().setService(CompatibilitySearchService.class).setAutoConfig(false).setRequired(false))
        );

        logger.debug("Finished initializing Rest-V2 Activator!");
    }
}
