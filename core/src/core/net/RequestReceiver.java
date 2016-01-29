package core.net;

import core.net.NodeSocket;
import core.net.RequestListener;

import core.req.Request;
import core.req.InvalidMessageException;

import core.util.Worker;

import java.util.ArrayList;
import java.io.IOException;

import java.net.SocketException;

/**
 * Request Listener
 *
 * Listens on a port for a message and notifies any listeners when
 * a request is received
 */
public class RequestReceiver extends Worker {

    /**
     * Socket to list to for incoming requests
     */
    private NodeSocket socket;

    /**
     * Listeners for incoming requests
     */
    private ArrayList<RequestListener> listeners;

    /**
     * Constructs a new request receiver to listen for requests on
     * the given port
     *
     * @param port - Port to listen for requests on
     *
     * @throws SocketException - If the port cannot be bound to
     */
    public RequestReceiver (int port) throws SocketException {
        super();
        this.listeners = new ArrayList<RequestListener>();
        this.socket = new NodeSocket(port);
    }

    public void addRequestListener(RequestListener handler){
        this.listeners.add(handler);
    }

    public void listen () throws IOException, InvalidMessageException {
        try {
            Request req = (Request) this.socket.receive();

            for (RequestListener handler : this.listeners){
                handler.handleRequest(req, socket.getAddress());
            }
        } catch (SocketException ex){
            if (this.isRunning()){
                throw ex;
            }
        }
    }

    public void setup () {}

    public void teardown () {
        this.socket.close();
    };

    public void execute () {
        try {
            this.listen();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

}