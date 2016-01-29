
package core.util;

/**
 * Abstract base class for asynchronous jobs
 */
public abstract class Worker implements Runnable {

    private boolean running;

    public Worker(){
        this.running = false;
    }

    /**
     * Request that this job stop running
     * this will not stop the job if it is blocked for i/o, and requires derived
     * classes to check their status
     */
    public void stop (){
        this.setRunning(false);
    }

    /**
     * runs this job asynchronously
     */
    public void start () {
        (new Thread(this)).start();
    }

    /**
     * execute
     * sub classes override and include logic to be execute asynchronously
     */
    public abstract void execute ();
    
    /**
     * Perform any setup tasks before running the job
     */
    public abstract void setup ();
    
    /**
     * Perform any shutdown tasks before shutting down
     */
    public abstract void teardown ();

    /**
     * isRunning
     * @return whether the job is currently running or not
     */
    public synchronized boolean isRunning (){
        return this.running;
    }

    /**
     * run
     * Starts the asynchronous task
     */
    public void run () {
        this.setup();
        this.setRunning(true);
        while (this.isRunning()){
            this.execute();
        }
        this.teardown();
    }

    /**
     * Set the running flag
     * Synchronized so that multiple threads stopping/starting this job are safe
     * @param running Value of the running flag
     */
    private synchronized void setRunning(boolean running){
        this.running = running;
    }

}
