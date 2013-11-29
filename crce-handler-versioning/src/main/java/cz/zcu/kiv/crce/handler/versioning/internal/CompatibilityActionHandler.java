package cz.zcu.kiv.crce.handler.versioning.internal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.service.CompatibilityService;
import cz.zcu.kiv.crce.concurrency.model.Task;
import cz.zcu.kiv.crce.concurrency.service.TaskRunnerService;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;

/**
 * Plugin which creates compatibility data for newly added resources.
 *
 * The plugin also ensures the compatibility data are removed after the resource is
 * deleted from CRCE.
 *
 * Date: 29.11.13
 *
 * @author Jakub Danek
 */
public class CompatibilityActionHandler extends AbstractActionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CompatibilityActionHandler.class);

    private volatile CompatibilityService m_compatibilityService; //injected by dependency manager
    private volatile TaskRunnerService m_taskRunnerService;   /* injected by dependency manager */

    @Override
    public Resource beforeDeleteFromStore(Resource resource, Store store) {
        logger.debug("BeforeDeleteFromStore called.");
        if(resource == null) {
            return resource;
        }

        m_compatibilityService.removeCompatibilities(resource);
        logger.debug("Compatibility data about resource {} delted.", resource.getSymbolicName());

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
        for(Resource resource : resources) {
            Task compTask = new CompatibilityCalculationTask(resource.getId(), resource);
            m_taskRunnerService.scheduleTask(compTask);
            logger.debug("Task planned.");
        }
        return resources;
    }


}
