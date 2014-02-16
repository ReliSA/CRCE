package cz.zcu.kiv.crce.handler.versioning.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.service.CompatibilityService;
import cz.zcu.kiv.crce.concurrency.model.Task;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 * Background task for calculation of compatibility data for a Resource.
 *
 * Creates compatibility data for all resources with the same crce.identity name and lower version.
 *
 * Date: 19.11.13
 *
 * @author Jakub Danek
 */
public class CompatibilityCalculationTask extends Task<Object> {
    private static final Logger logger = LoggerFactory.getLogger(CompatibilityCalculationTask.class);

    private volatile CompatibilityService compatibilityService;      //injected by dependency manager

    private volatile MetadataService metadataService; //injected by dependency manager

    private final Resource resource;

    /**
     *
     * @param id ID of the task, for tracking
     * @param resource resource for which the compatibility data shall be computed
     */
    public CompatibilityCalculationTask(String id, Resource resource) {
        super(id, "Calculates compatibility data for a provided resource.", "crce-handler-versioning");
        this.resource = resource;
    }

    @Override
    protected Object run() throws Exception {
        String symbolicName = metadataService.getPresentationName(resource);
        logger.debug("Started calculation of Compatibility data for resource {}", symbolicName);
        Object o = compatibilityService.calculateCompatibilities(resource);
        logger.debug("Finished calculation of Compatibility data for resource {}", symbolicName);
        return o;
    }
}
