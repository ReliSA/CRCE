package cz.zcu.kiv.crce.concurrency.service;

import cz.zcu.kiv.crce.concurrency.model.Task;

/**
 * Public API for TaskRunner.
 *
 * Date: 11.11.13
 *
 * @author Jakub Danek
 */
public interface TaskRunnerService {

    /**
     * Schedule a task to be run in background when crce has available resources.
     *
     * There is no definition nor assurance of when the task will be run.
     *
     * @param task {@link Task instance containing the definition of the job to be done}
     * @throws {@link java.util.concurrent.RejectedExecutionException} when the task cannot be scheduled
     */
    public void scheduleTask(Task task);
}
