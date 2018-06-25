package cz.zcu.kiv.crce.concurrency.model;

/**
 * Class which allows chaining of jobs from multiple task classes.
 *
 * Date: 24.7.17
 *
 * @author Jakub Danek
 */
public abstract class ChainableTask<T> extends Task<T>  {

    public ChainableTask(String id, String description, String module) {
        super(id, description, module);
    }

    @Override
    protected final T run() throws Exception {
        Task<T> next = doWork();
        if(next != null) {
            return next.run();
        }

        return null;
    }

    /**
     *
     * @return the task to call next, or null if this is the last task
     * @throws Exception
     */
    protected abstract Task<T> doWork() throws Exception;
}
