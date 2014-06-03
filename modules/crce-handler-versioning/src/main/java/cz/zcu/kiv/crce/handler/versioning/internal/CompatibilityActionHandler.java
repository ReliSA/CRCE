package cz.zcu.kiv.crce.handler.versioning.internal;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.service.CompatibilityService;
import cz.zcu.kiv.crce.concurrency.model.Task;
import cz.zcu.kiv.crce.concurrency.service.TaskRunnerService;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;

/**
 * Plugin which creates compatibility data for newly added resources.
 * <p/>
 * The plugin also ensures the compatibility data are removed after the resource is
 * deleted from CRCE.
 * <p/>
 * Date: 29.11.13
 *
 * @author Jakub Danek
 */
@ParametersAreNonnullByDefault
public class CompatibilityActionHandler extends AbstractActionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CompatibilityActionHandler.class);

    static final String CATEGORY_COMPARED = "compared";

    private volatile CompatibilityService compatibilityService; //injected by dependency manager
    private volatile TaskRunnerService taskRunnerService;   /* injected by dependency manager */
    private volatile MetadataService metadataService; //injected by dependency manager

    @Override
    public Resource beforeDeleteFromStore(Resource resource, Store store) {
        logger.debug("BeforeDeleteFromStore called.");
        if (resource == null) {
            return resource;
        }

        compatibilityService.removeCompatibilities(resource);
        String symbolicName = metadataService.getSingletonCapability(resource, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY).getAttributeStringValue(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME);
        logger.debug("Compatibility data about resource {} delted.", symbolicName);

        return resource;
    }

    @Override
    public List<Resource> afterBufferCommit(List<Resource> resources, Buffer buffer, Store store) {
        logger.debug("AfterBufferCommit called.");
        /*
            After the resource is put to store, start calculation of its compatibility data.
         */
        if (resources == null) {
            return resources;
        }
        for (Resource resource : resources) {
            if (versionable(resource)) {
                Task compTask = new CompatibilityCalculationTask(resource.getId(), resource);
                taskRunnerService.scheduleTask(compTask);
                logger.debug("Task planned.");
            } else {
                logger.debug("Resource not versionable, skipping...");
            }
        }
        return resources;
    }

    private boolean versionable(Resource res) {
        List<String> categories = metadataService.getCategories(res);
        for (int i = 0; i < categories.size(); i++) {
            if (VersioningActionHandler.CATEGORY_VERSIONED.equals(categories.get(i))) {
                return true;
            }
        }

        return false;
    }
}
