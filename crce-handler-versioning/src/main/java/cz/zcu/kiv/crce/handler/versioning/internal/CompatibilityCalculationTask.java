package cz.zcu.kiv.crce.handler.versioning.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.service.CompatibilityService;
import cz.zcu.kiv.crce.concurrency.model.Task;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 * Background task for calculation of compatibility data for a Resource.
 * <p/>
 * Creates compatibility data for all resources with the same crce.identity name and lower version.
 * <p/>
 * Date: 19.11.13
 *
 * @author Jakub Danek
 */
public class CompatibilityCalculationTask extends Task<Object> {
    private static final Logger logger = LoggerFactory.getLogger(CompatibilityCalculationTask.class);

    private Resource resource;

    /**
     * @param id       ID of the task, for tracking
     * @param resource resource for which the compatibility data shall be computed
     */
    public CompatibilityCalculationTask(String id, Resource resource) {
        super(id, "Calculates compatibility data for a provided resource.", "crce-handler-versioning");
        this.resource = resource;
    }

    @Override
    protected Object run() throws Exception {
        String symbolicName = getMetadataService().getPresentationName(resource);
        logger.debug("Started calculation of Compatibility data for resource {}", symbolicName);
        Object o = getCompatibilityService().calculateCompatibilities(resource);
        logger.debug("Finished calculation of Compatibility data for resource {}", symbolicName);

        //TODO thread unsafe, this needs to be solved on architecture level!!!
        resource = getResourceDao().loadResource(getMetadataService().getUri(resource));
        getMetadataService().addCategory(resource, CompatibilityActionHandler.CATEGORY_COMPARED);
        getResourceDao().saveResource(resource);

        logger.debug("Category \"compared\" set for resource {}", symbolicName);
        return o;
    }

    private CompatibilityService getCompatibilityService() {
        return getService(CompatibilityService.class);
    }

    private MetadataService getMetadataService() {
        return getService(MetadataService.class);
    }

    private ResourceDAO getResourceDao() {
        return getService(ResourceDAO.class);
    }

    private <S> S getService(Class<S> clazz) {
        // get bundle instance via the OSGi Framework Util class
        BundleContext ctx = FrameworkUtil.getBundle(CompatibilityCalculationTask.class).getBundleContext();
        ServiceReference serviceReference = ctx.getServiceReference(clazz.getName());
        return clazz.cast(ctx.getService(serviceReference));
    }
}
