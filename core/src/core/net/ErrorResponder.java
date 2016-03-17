package core.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import core.req.ErrorMessage;

public class ErrorResponder implements Runnable {

	private ErrorMessage errorMsg;
	private NodeSocket socket;
	private ArrayList<TransferListener> listeners;
	
	public ErrorResponder(ErrorMessage msg, SocketAddress address) throws SocketException {
		this.errorMsg = msg;
		this.socket = new NodeSocket(address);
		this.listeners = new ArrayList<TransferListener>();
	}
	
	@Override
	public void run() {
		try {
			notifyError(errorMsg);
			this.socket.send(this.errorMsg);
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
