package cz.zcu.kiv.crce.rest.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.rest.internal.convertor.ConvertorToBeans;
import cz.zcu.kiv.crce.rest.internal.convertor.MimeTypeSelector;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "UWF_UNWRITTEN_FIELD", justification = "Injected by dependency manager.")
public final class Activator extends DependencyActivatorBase {

    private static volatile Activator instance;
    private volatile Store store;                       /*
     * injected by dependency manager
     */

    private volatile MetadataService metadataService;   /*
     * injected by dependency manager
     */

    private volatile ConvertorToBeans convertorToBeans; /*
     * injected by dependency manager
     */

    private volatile MimeTypeSelector mimeTypeSelector; /*
     * injected by dependency manager
     */


    public static Activator instance() {
        return instance;
    }

    public Store getStore() {
        return store;
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

    public ConvertorToBeans getConvertorToBeans() {
        return convertorToBeans;
    }

    public MimeTypeSelector getMimeTypeSelector() {
        return mimeTypeSelector;
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {

    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD",
            justification = "Dependency manager workaround.")
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        instance = this;

        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                .add(createServiceDependency().setService(ConvertorToBeans.class).setRequired(true))
                .add(createServiceDependency().setService(MimeTypeSelector.class).setRequired(true))
        );
    }
}
