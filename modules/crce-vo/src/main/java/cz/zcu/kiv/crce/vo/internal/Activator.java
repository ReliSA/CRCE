package cz.zcu.kiv.crce.vo.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.vo.internal.service.MappingServiceDozer;
import cz.zcu.kiv.crce.vo.service.MappingService;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "UWF_UNWRITTEN_FIELD", justification = "Injected by dependency manager.")
public final class Activator extends DependencyActivatorBase {

    private static volatile Activator instance;

    /*
     * Injected by Dependency Manager.
     */
    private volatile MetadataService metadataService;

    public static Activator instance() {
        return instance;
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

     @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {

    }

    @Override
    @edu.umd.cs.findbugs.annotations
            .SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Dependency manager workaround.")
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        instance = this;

        manager.add(createComponent()
                        .setInterface(MappingService.class.getName(), null)
                        .setImplementation(MappingServiceDozer.class)
                        .add(createServiceDependency().setService(MetadataService.class).setRequired(true)));

        manager.add(createComponent()
                        .setImplementation(this)
                        .add(createServiceDependency().setService(MetadataService.class).setRequired(true))
        );
    }
}
