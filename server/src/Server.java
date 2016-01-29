
import core.net.RequestListener;
import core.ctrl.RequestController;

import core.req.Message;

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
    public void stop(){
    	super.stop();
    	this.listener.stop();
    	this.listener.teardown();
    }

    @Override
    public void start(){
    	super.start();
        listener.start();
    }

    @Override
    public void usage () {

    }

    public void handleMessage(Message msg){
    }

    public void handleComplete () {
    }

    public void handleStart (){
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
