
import java.net.SocketException;
import java.util.logging.Level;
import core.ctrl.RequestController;
import core.req.ErrorMessage;
import core.req.Message;

public class Server extends RequestController {

    private static final int SERVER_PORT = 69;

    /**
     * Tracks the number of incomplete transfers
     */
    private int activeTransferCount;

    public Server (String[] commandLineArgs) throws SocketException {
        super(SERVER_PORT, commandLineArgs);
        this.activeTransferCount = 0;
    }

    @Override
    public void usage() {
        System.out.println("TFTP Server");
        System.out.println("  Commands:");
        System.out.println("    help           Prints this message");
        System.out.println("    chgdir         Change the working directory (source and destination directory of files)");
        System.out.println("    shutdown       Exits the server");      
    }

    @Override
    public void handleMessage(Message msg){
        LOGGER.log(Level.FINE, "Received message: " + msg.toString());
    }

    @Override
    public void handleSendMessage(Message msg) {
        LOGGER.log(Level.FINE, "Sending message: " + msg.toString());
    }

    @Override
    public synchronized void handleComplete () {
        this.cli.message("\nCompleted a transfer");
        this.cli.prompt();
        this.activeTransferCount--;
    }

    public synchronized void handleErrorMessage (ErrorMessage err){
        LOGGER.log(Level.SEVERE, "Error message: " + err.toString());
        this.cli.message("\nFinished transfer with errors");
        this.activeTransferCount--;
    }

    public synchronized void handleStart (){
        this.cli.message("\nStarting transfer");
        this.cli.prompt();
        this.activeTransferCount++;
    }

    @Override
    public synchronized void stop(){
        if (this.activeTransferCount != 0){
            this.cli.message("There are currently " + this.activeTransferCount +
                    " transfers still running. The server will shut down after all transfers have completed.");
        }
        super.stop();
    }

    public static void main (String[] args) {
        Server server;

        try {

            server = new Server(args);
            server.start();
        } catch (SocketException e){
            System.out.println("Socket could not bind to port: " + SERVER_PORT);
            System.out.println("Ensure that you have sufficient privileges to bind to this port");
        }
    }

    @Override
    public void handleException(Exception e) {
        LOGGER.log(Level.SEVERE,e.getMessage());
        this.cli.message("Finished transfer with errors");
        this.activeTransferCount--;
    }

    @Override
    public void handleInfo(String info) {
        LOGGER.log(Level.INFO,info);

    }
}
