import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.logging.Level;

import core.ctrl.Controller;
import core.log.Logger;
import core.net.RequestHandler;
import core.net.RequestListener;
import core.net.TransferListener;
import core.req.Message;
import core.req.Request;


public class ErrorSimulator extends Controller implements TransferListener {

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
        System.out.println("Usage:\n\tShutdown\n\tHelp");
    }
	
    
    public static void main(String[] args) {
    	Logger.init(System.out,Level.INFO);
    	ErrorSimulator simulator;
        try {
			simulator= new ErrorSimulator();
			simulator.start();
		} catch (SocketException e) {
			Logger.log("ErrorSimulator", Level.SEVERE, "Socket could not bind to port: " + SIMULATOR_PORT);
		}
    }
	
}
