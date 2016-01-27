
import core.ctrl.RequestController;

import java.net.SocketException;

public class Server extends RequestController {

    private static final int SERVER_PORT = 69;

    public Server () throws SocketException {
        super(SERVER_PORT);
    }

    @Override
    public void usage () {

    }

    public static void main (String[] args) {
        Server server;

        try {
            server = new Server();
            server.start();
        } catch (SocketException e){
            System.out.println("Socket could not bind to port: " + SERVER_PORT);
            System.out.println("Ensure that you have sufficient privileges to bind to this port");
        }
    }

}
