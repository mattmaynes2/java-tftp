package core.ctrl;

import java.net.SocketAddress;
import java.net.SocketException;
import java.util.logging.Level;

import core.log.Logger;
import core.net.ReadTransfer;
import core.net.RequestListener;
import core.net.RequestReceiver;
import core.net.WriteTransfer;
import core.req.AckMessage;
import core.req.Request;

/**
 * Request Controller
 *
 * Responds to transfer requests and performs operations
 */
public abstract class RequestController extends Controller implements RequestListener {
	
    /**
     * Handles sockets requests
     */
    private RequestReceiver receiver;
   
    /**
     * Constructs a new request controller for handling transfer requests
     *
     * @param port - Port to listen for requests on
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
    	Logger.log(Level.FINE, "Received request from client " + req.toString());

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
        WriteTransfer runner;

        try {
            runner = new WriteTransfer(address, filename);
            runner.addTransferListener(this);
            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
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
            // Send the initial Ack
            runner.getSocket().send(new AckMessage((short)0));

            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

    }
}
