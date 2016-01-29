import java.net.SocketException;
import java.util.logging.Level;
import core.ctrl.Controller;
import core.log.Logger;
import core.net.TransferListener;
import core.req.Message;


public class ErrorSimulator extends Controller {

    public static final int SIMULATOR_PORT = 68;

    private ReceiveWorker recieveListener;

    public ErrorSimulator() throws SocketException  {
        recieveListener = new ReceiveWorker(SIMULATOR_PORT);
    }

    public void handleComplete () {}

    public void handleMessage(Message msg){
        System.out.println(msg);
    }
    public void handleStart () {}

    @Override
    public void start() {
        super.start();
        recieveListener.start();
    }

    @Override
    public void stop() {
        super.stop();
        recieveListener.stop();
        recieveListener.teardown();
    }

    @Override
    public void usage() {
        System.out.println("TFTP Error Simulator");
        System.out.println("  Commands:");
        System.out.println("    help           Prints this message");
        System.out.println("    shutdown       Exits the simulator");
    }

    public static void main(String[] args) {
        Logger.init(Level.INFO);
        ErrorSimulator simulator;
        try {
            simulator= new ErrorSimulator();
            simulator.start();
        } catch (SocketException e) {
            Logger.log(Level.SEVERE, "Socket could not bind to port: " + SIMULATOR_PORT);
        }
    }

    @Override
    public void handleSendMessage(Message msg) {

    }

}
