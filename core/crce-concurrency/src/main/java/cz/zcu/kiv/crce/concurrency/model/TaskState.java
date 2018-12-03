package cz.zcu.kiv.crce.concurrency.model;

/**
 * Enumeration for marking the state of a particular Task.
 *
 * Date: 13.11.13
 *
 * @author Jakub Danek
 */
public enum TaskState {
    /**
     * The task has been created and potentially scheduled.
     */
    CREATED,
    /**
     * The task has started its job.
     */
    RUNNING,
    /**
     * The task has finished its job.
     */
    FINISHED
}
