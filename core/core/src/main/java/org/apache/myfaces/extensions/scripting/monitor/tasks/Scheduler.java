package org.apache.myfaces.extensions.scripting.monitor.tasks;

/**
 *
 */
public interface Scheduler {

    public void schedule(Runnable command);
    
    public void shutdown();

}
