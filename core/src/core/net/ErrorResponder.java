package core.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import core.req.ErrorMessage;
import core.req.Message;

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
			notifySendMessage(errorMsg);
			this.socket.send(this.errorMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addListener(TransferListener listener) {
		this.listeners.add(listener);
	}
	
	public void notifySendMessage(Message msg) {
		for (TransferListener listener : this.listeners) {
            listener.handleSendMessage(msg);
        }
	}

}
