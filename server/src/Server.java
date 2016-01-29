
import core.net.RequestReceiver;
import core.ctrl.RequestController;

import core.req.Message;

import java.net.SocketException;
import core.log.Logger;
import core.log.Logger;
import java.util.logging.Level;

public class Server extends RequestController {

    private static final int SERVER_PORT = 69;

    public Server () throws SocketException {
        super(SERVER_PORT);
    }

    @Override
    public void usage () {

    }

    public void handleMessage(Message msg){
        Logger.log(Level.FINE, "Received message: " + msg.toString());
    }

    @Override
    public void handleSendMessage(Message msg) {
        Logger.log(Level.FINE, "Sending message: " + msg.toString());
    }


    public void handleComplete () {
        this.cli.message("Completed a transfer");
    }

    public void handleStart (){
        this.cli.message("Starting transfer");
    }

    public static void main (String[] args) {
        Server server;
        Logger.init(Level.ALL);

        try {

            server = new Server();
            server.start();
        } catch (SocketException e){
            System.out.println("Socket could not bind to port: " + SERVER_PORT);
            System.out.println("Ensure that you have sufficient privileges to bind to this port");
        }
    }
}
