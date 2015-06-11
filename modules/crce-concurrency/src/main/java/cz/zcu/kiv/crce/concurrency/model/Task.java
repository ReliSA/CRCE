package cz.zcu.kiv.crce.concurrency.model;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task base class to be used by client modules. Override this class to provide
 * the actual code of the job to be done in background.
 *
 * @param <T> Job result type
 *
 * Date: 11.11.13
 *
 * @author Jakub Danek
 */
public abstract class Task<T> implements Callable<T> {

    private static final Logger logger = LoggerFactory.getLogger(Task.class);

    private final String id;
    private final String description;
    private final String caller;
    private TaskState state;

    /**
     * Initialize Task description for easy tracking. Keep in mind that id,module pair should be always
     * unique!
     *
     * @param id job ID
     * @param description job description
     * @param module calling module - for easy tracking of where the Task has come from
     */
    protected Task(String id, String description, String module) {
        this.id = id;
        this.description = description;
        this.caller = module;
        setState(TaskState.CREATED);
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCaller() {
        return caller;
    }

    /**
     *
     * @return state the task finds itself in currently
     */
    public synchronized TaskState getState() {
        return state;
    }

    public final synchronized void setState(TaskState state) {
        this.state = state;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public final T call() throws Exception {
        setState(TaskState.RUNNING);
        logger.info("Task {} called by {} has been started.", id, caller);
        logger.debug("Task description: {}.", description);
        T obj;
        long duration;
        try {
            long start = System.nanoTime();
            obj = run();
            duration = System.nanoTime() - start;
        } catch (Throwable t) {
            logger.error("Error while executing task {}.", id, t);
            throw t;
        }
        setState(TaskState.FINISHED);   //should be set AFTER returning the object
        // probably will require listener thread for the TaskRunner
        logger.info("Task {} called by {} has finished in {} ms.", id, caller, TimeUnit.NANOSECONDS.toMillis(duration));
        return obj;
    }

    /**
     * Override this to provide job's logic. Optionaly the job can return a result.
     * @return
     * @throws Exception
     */
    protected abstract T run() throws Exception;
}
