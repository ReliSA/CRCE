package cz.zcu.kiv.crce.concurrency.service.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.concurrency.model.Task;
import cz.zcu.kiv.crce.concurrency.internal.TaskRunner;
import cz.zcu.kiv.crce.concurrency.service.TaskRunnerService;

/**
 * Date: 11.11.13
 *
 * @author Jakub Danek
 */
public class TaskRunnerServiceImpl implements TaskRunnerService {
    private static final Logger logger = LoggerFactory.getLogger(TaskRunnerServiceImpl.class);

    /**
     * Schedule a task to be run in background when crce has available resources.
     * <p/>
     * There is no definition nor assurance of when the task will be run and user may not
     * count on that.
     *
     * @param task {@link cz.zcu.kiv.crce.concurrency.model.Task instance containing the definition of the job to be done}
     */
    @Override
    public void scheduleTask(Task task) {
        logger.debug("Service scheduleTask(task) has been called.");
        TaskRunner.get().scheduleTask(task);
    }
}
