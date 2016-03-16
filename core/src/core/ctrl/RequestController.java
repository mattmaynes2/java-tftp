package core.ctrl;

import java.io.File;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.logging.Level;

import core.net.ErrorResponder;
import core.net.ReadTransfer;
import core.net.RequestListener;
import core.net.RequestReceiver;
import core.net.Transfer;
import core.net.TransferListener;
import core.net.WriteTransfer;
import core.req.AckMessage;
import core.req.ErrorCode;
import core.req.Request;
import core.req.ErrorMessage;

/**
 * Request Controller
 *
 * Responds to transfer requests and performs operations
 */
public abstract class RequestController extends Controller implements RequestListener, TransferListener  {

    /**
     * Handles sockets requests
     */
    private RequestReceiver receiver;

    /**
     * Constructs a new request controller for handling transfer requests
     *
     * @param port - Port to listen for requests on
     * @param commandLineArgs - Arguments input from the command line
     *
     * @throws SocketException - If the socket on the desired port is in use
     */
    public RequestController (int port, String[] commandLineArgs) throws SocketException {
        super(commandLineArgs);
        this.receiver = new RequestReceiver(port);
        this.receiver.addRequestListener(this);
    }

    /**
     * Handles transfer requests and performs file transfer
     *
     * @param req - File transfer request
     * @param address - Sender's address
     */
    public void handleRequest (Request req, SocketAddress address){
    	LOGGER.log(Level.FINE, "Received request from client " + req.toString());

        switch(req.getOpCode()){
            case READ:
                this.read(address, req.getFilename());
                break;
            case WRITE:
                this.write(address, req.getFilename());
                break;
            default:
            	break;
        }
    }

    /**
     * Handles invalid transfer requests
     *
     * @param err - Error message
     */
    public void handleError (ErrorMessage err) {
        LOGGER.log(Level.WARNING, "Invalid request received:"+err.getMessage());
    }

    /**
     * Starts the controller's command line interface
     */
    @Override
    public void start (){
        super.start();
        this.receiver.start();
    }

    /**
     * Stops the controller and closes the receiving socket
     */
    @Override
    public void stop (){
        super.stop();
        this.receiver.stop();
        this.receiver.teardown();
    }

    /**
     * Performs a write transfer by reading a file and writing it to the
     * alternate endpoint
     *
     * @param address - Address to send file
     * @param filename - Name of file to transfer
     */
    public void read (SocketAddress address, String filename){
        Transfer runner;
        ErrorResponder responder;
        // Start an error thread that will send an error code 1 message to the client
        if(!(new File(filename).isFile())) {
        	ErrorMessage msg = new ErrorMessage(ErrorCode.FILE_NOT_FOUND, "\"" + filename + "\" not found.");
			try {
				responder = new ErrorResponder(msg, address);
				responder.addListener(this);
				(new Thread(responder)).start(); 	
			} catch (SocketException e) {
				e.printStackTrace();
			}
        } else { // Otherwise start the transfer
	        try {
	            runner = new WriteTransfer(address, filename);
	            runner.addTransferListener(this);
	            (new Thread(runner)).start();
	        } catch (Exception e){
	            e.printStackTrace();
	            System.exit(1);
	        }
        }
    }

    /**
     * Performs a read transfer by writing a file that is provided
     * over a socket with the given address
     *
     * @param address - Address to receive file over
     * @param filename - Name of file to receive
     */
    public void write (SocketAddress address, String filename){
        ReadTransfer runner;

        try {
            runner = new ReadTransfer(address, filename);

            runner.addTransferListener(this);
            AckMessage ack = new AckMessage((short)0);
            this.handleSendMessage(ack);
            // Send the initial Ack
            runner.getSocket().send(ack);
            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

    }
}
