package org.apache.myfaces.extensions.scripting.monitor.tasks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * <p>Test class for
 * <code>org.apache.myfaces.extensions.scripting.monitor.tasks.ConcurrencyUtilsScheduler</code>.</p>
 */
public class ConcurrencyUtilsSchedulerTest {

    /** The scheduler instance that we want to test */
    private Scheduler scheduler;

    // ------------------------------------------ Test lifecycle methods

    /**
     * <p>Initializes the scheduler instance that we want to test.</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setUp() throws Exception {
        scheduler = new ConcurrencyUtilsScheduler(100);
    }

    /**
     * <p>Shuts down the scheduler instance that we wanted to test.</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void tearDown() throws Exception {
        scheduler.shutdown();
    }

    // ------------------------------------------ Test methods

    @Test
    public void testScheduleTask() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);

        scheduler.schedule(new Runnable() {
            public void run() {
                counter.incrementAndGet();
            }
        });

        Thread.sleep(1000);

        assertTrue("The runnable task hasn't been called often enough.",
                counter.intValue() >= 10);

        scheduler.shutdown();
    }

    @Test
    public void testScheduleTimespan() throws Exception {
        Scheduler scheduler = new ConcurrencyUtilsScheduler(100);
        scheduler.schedule(new TimespanTest());

        Thread.sleep(1000);

        scheduler.shutdown();
    }

    // ------------------------------------------ Utility classes

    private class TimespanTest implements Runnable {

        /** The last time that the runnable method has been called, or this object has been initialized. */
        private long timestamp = System.currentTimeMillis();

        /**
         * <p>Callback that will be called by the scheduler.</p>
         */
        public void run() {
            long current = System.currentTimeMillis();

            // Test whether the timespan that has passed is between 90 and 110 milliseconds
            long timespan = current - timestamp;
            if (timespan < 90 || timespan > 110) {
                fail(String.format("The timespan that has passed is " +
                        "longer or shorter than expected: %s ms", timespan));
            } else {
                timestamp = current;
            }
        }
    }

}
