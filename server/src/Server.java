
import core.net.RequestListener;
import core.run.RequestController;

import java.net.SocketException;

public class Server extends RequestController {

    private static final int SERVER_PORT = 69;

    private RequestListener listener;
    
    public Server () throws SocketException {
        super();
        this.listener = new RequestListener(SERVER_PORT);
        listener.addRequestHandler(this);
    }

    @Override
    public void start(){
    	super.start();
        listener.execute();
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
