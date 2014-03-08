package cz.zcu.kiv.crce.rest.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;

import org.osgi.framework.BundleContext;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.osgi.util.FilterParser;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.rest.internal.convertor.JaxbMapping;
import cz.zcu.kiv.crce.rest.internal.convertor.MimeTypeSelector;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "UWF_UNWRITTEN_FIELD", justification = "Injected by dependency manager.")
public final class Activator extends DependencyActivatorBase {

    private static volatile Activator instance;

    /*
     * Injected by Dependency Manager.
     */
    private volatile Store store;
    private volatile MetadataService metadataService;
    private volatile JaxbMapping convertorToBeans;
    private volatile MimeTypeSelector mimeTypeSelector;
    private volatile MetadataFactory metadataFactory;
    private volatile FilterParser filterParser;

    public static Activator instance() {
        return instance;
    }

    public Store getStore() {
        return store;
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

    public JaxbMapping getConvertorToBeans() {
        return convertorToBeans;
    }

    public MimeTypeSelector getMimeTypeSelector() {
        return mimeTypeSelector;
    }

    public MetadataFactory getMetadataFactory() {
        return metadataFactory;
    }

    public FilterParser getFilterParser() {
        return filterParser;
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {

    }

    @Override
    @edu.umd.cs.findbugs.annotations
            .SuppressWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Dependency manager workaround.")
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        instance = this;

        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataService.class).setRequired(true))
                .add(createServiceDependency().setService(JaxbMapping.class).setRequired(true))
                .add(createServiceDependency().setService(MimeTypeSelector.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataFactory.class).setRequired(true))
                .add(createServiceDependency().setService(FilterParser.class).setRequired(true))
        );
    }
}
