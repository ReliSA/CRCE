package cz.zcu.kiv.crce.concurrency.internal;

import static org.junit.Assert.*;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cz.zcu.kiv.crce.concurrency.model.Task;
import cz.zcu.kiv.crce.concurrency.model.TaskState;

/**
 * TaskRunner functional tests.
 *
 * Date: 14.11.13
 *
 * @author Jakub Danek
 */
public class TaskRunnerTest {

    private TaskRunner runner;

    /**
     * Simple task for testing purposes.
     *
     * Adds two numbers and waits for particular amount of time.
     */
    private class TestTask extends Task<Integer> {

        private int a, b;
        private long millisToSleep;

        private TestTask(String id, String description, int a, int b, long millisToSleep) {
            super(id, description, "test");
            this.a = a;
            this.b = b;
            this.millisToSleep = millisToSleep;
        }

        @Override
        protected Integer run() throws Exception {
            Thread.sleep(millisToSleep);
            return a + b;
        }
    }

    @Before
    public void before() {
        //yes, this is one of the typical reasons why singleton pattern is WRONG.
        TaskRunner.init(3);
        runner = TaskRunner.get();
    }

    @After
    public void after() {
        runner.stop();
    }

    /**
     * Test that scheduler starts a Task and the Task does its job.
     * @throws Exception
     */
    @Test
    public void taskRunTest() throws Exception{
        Task t = new TestTask("taskRunTest", "Simple task testing rung.", 1, 1, 0);
        Future<Integer> f = runner.scheduleTask(t);
        try {
            int result = f.get(3, TimeUnit.SECONDS);
            assertEquals("How can one make an error in a 1 + 1 calculation?", 2, result);
        } catch (TimeoutException e) { //just let other exception bubble up
            fail("Simple Task took too long to run, something must have gone wrong.");
        }
    }

    /**
     * Test that only allowed number of threads is spawned.
     *
     * WARNING: This tests heavily depends on Thread.sleep() functionality. That might potentially lead to instability.
     * Might save you some time of debugging in case of failure.
     * @throws Exception
     */
    @Test
    public void resourceUsageTest() throws Exception {
        Task t1, t2, t3, t4;
        Future<Integer> f1, f2, f3, f4;

        t1 = new TestTask("resourceUsageTest1", "Sloooooow task", 5, 5, 3000);
        t2 = new TestTask("resourceUsageTest2", "Sloooooow task", 5, 5, 3000);
        t3 = new TestTask("resourceUsageTest3", "Sloooooow task", 5, 5, 3000);
        t4 = new TestTask("resourceUsageTest4", "Speedy Gonzales", 6, 6, 0);

        f1 = runner.scheduleTask(t1);
        f2 = runner.scheduleTask(t2);
        f3 = runner.scheduleTask(t3);
        f4 = runner.scheduleTask(t4);

        assertFalse(f1.isDone());
        assertFalse(f2.isDone());
        assertFalse(f3.isDone());
        assertFalse(f4.isDone());

        Thread.sleep(100);//give scheduler some time to actually start the tasks

        //only the first 3 are running now
        assertEquals("Expected to be running!", TaskState.RUNNING, t1.getState());
        assertEquals("Expected to be running!", TaskState.RUNNING, t2.getState());
        assertEquals("Expected to be running!", TaskState.RUNNING, t3.getState());
        assertEquals("Expected to be waiting!", TaskState.CREATED, t4.getState());

        try {
            f4.get(6, TimeUnit.SECONDS); //wait until t4 is finished
            f1.get(1, TimeUnit.SECONDS);
            f2.get(1, TimeUnit.SECONDS);
            f3.get(1, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            fail("Tasks took too long to run, something must have gone wrong.");
        }

        //all finished now
        assertEquals("Expected to be running!", TaskState.FINISHED, t1.getState());
        assertEquals("Expected to be running!", TaskState.FINISHED, t2.getState());
        assertEquals("Expected to be running!", TaskState.FINISHED, t3.getState());
        assertEquals("Expected to be waiting!", TaskState.FINISHED, t4.getState());

        //final check that all the calculations went through correctly
        int result = f1.get() + f2.get() + f3.get() + f4.get();
        assertEquals("It's all clear now. The Life, the Universe, and (the) Everything.", 42, result);

    }


}
