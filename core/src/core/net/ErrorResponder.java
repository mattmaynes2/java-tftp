package core.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import core.req.ErrorMessage;

/**
 * ErrorResponder
 * 
 * Runnable used in place of a transfer when there is an error that occurs before the
 * transfer has begun. Sends an error message to the external endpoint of the transfer.
 */
public class ErrorResponder implements Runnable {

	/**
	 * The message that will be sent in response to an error
	 */
    private ErrorMessage errorMsg;
    
    /**
     * The socket used to send the error message packet
     */
    private NodeSocket socket;
    
    /**
     * The listeners that need to be notified of the actions made by this thread
     */
    private ArrayList<TransferListener> listeners;

    /**
     * Constructs a new runnable that will be used to send an error 
     * message to a specified address
     * 
     * @param msg - The error message that will be sent to the external endpoint
     * @param address - The socket address of the external endpoint
     * 
     * @throws SocketException - If the socket cannot be created
     */
    public ErrorResponder(ErrorMessage msg, SocketAddress address) throws SocketException {
        this.errorMsg = msg;
        this.socket = new NodeSocket(address);
        this.listeners = new ArrayList<TransferListener>();
    }

    @Override
    public void run() {
        try {
        	this.notifyStart();
            this.socket.send(this.errorMsg);
            this.notifyError(errorMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a listener to the message being sent
     *
     * @param listener - The listener to add to the message that will be sent
     */
    public void addListener(TransferListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Notify the listeners that the start of the error message transmission has started
     */
    public void notifyStart() {
    	for (TransferListener listener : this.listeners) {
    		listener.handleStart();
    	}
    }
    
    /**
     * Notify the listeners that the error message has been sent
     *
     * @param msg - The error message that is being sent
     */
    public void notifyError(ErrorMessage msg) {
        for (TransferListener listener : this.listeners) {
            listener.handleErrorMessage(msg);
        }
    }
}
