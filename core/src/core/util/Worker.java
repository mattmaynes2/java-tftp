
package core.util;

public abstract class Worker implements Runnable {

    private boolean running;

    public Worker(){
        this.running = false;
    }

    public void stop (){
        this.setRunning(false);
    }

    public void start () {
        (new Thread(this)).start();
    }

    public abstract void execute ();
    public abstract void setup ();
    public abstract void teardown ();

    public synchronized boolean isRunning (){
        return this.running;
    }

    public void run () {
        this.setup();
        this.setRunning(true);
        while (this.isRunning()){
            this.execute();
        }
        this.teardown();
    }

    private synchronized void setRunning(boolean running){
        this.running = running;
    }

}
