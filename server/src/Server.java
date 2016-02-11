
import java.net.SocketException;
import java.util.logging.Level;

import core.ctrl.RequestController;
import core.log.Logger;
import core.req.ErrorMessage;
import core.req.Message;

public class Server extends RequestController {

    private static final int SERVER_PORT = 69;

    public Server (String[] commandLineArgs) throws SocketException {
        super(SERVER_PORT, commandLineArgs);
    }

    @Override
    public void usage() {
        System.out.println("TFTP Server");
        System.out.println("  Commands:");
        System.out.println("    help           Prints this message");
        System.out.println("    shutdown       Exits the server");
    }


    public void handleMessage(Message msg){
        Logger.log(Level.FINE, "Received message: " + msg.toString());
    }

    @Override
    public void handleSendMessage(Message msg) {
        Logger.log(Level.FINE, "Sending message: " + msg.toString());
    }


    public void handleComplete () {
        this.cli.message("\nCompleted a transfer");
        this.cli.prompt();
    }

    public void handleErrorMessage (ErrorMessage err){
        Logger.log(Level.SEVERE, "Received error message: " + err.toString());
    }

    public void handleStart (){
        this.cli.message("\nStarting transfer");
        this.cli.prompt();
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
}
