package core.net;

import core.net.NodeSocket;
import core.net.RequestHandler;

import core.req.Request;
import core.req.InvalidMessageException;

import core.util.Worker;

import java.util.ArrayList;
import java.io.IOException;

import java.net.SocketException;

public class RequestListener extends Worker {

    private NodeSocket socket;
    private ArrayList<RequestHandler> handlers;

    public RequestListener (int port) throws SocketException {
        super();
        handlers = new ArrayList<RequestHandler>();
        this.socket = new NodeSocket(port);
    }

    public void addRequestHandler(RequestHandler handler){
        this.handlers.add(handler);
    }

    public void listen () throws IOException, InvalidMessageException {
    	try {
	        Request req = (Request) this.socket.receive();

	        for (RequestHandler handler : this.handlers){
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
