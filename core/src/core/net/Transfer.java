package core.net;

import core.net.NodeSocket;
import core.net.TransferListener;

import core.req.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public abstract class Transfer implements Runnable {

    private ArrayList<TransferListener> listeners;
    public static final int BLOCK_SIZE = 512;

    protected Logger logger;

    private String filename;
    private NodeSocket socket;

    public Transfer (NodeSocket socket, String filename){
        this.socket = socket;
        this.filename = filename;
        this.listeners = new ArrayList<TransferListener>();
    }

    public String getFilename () {
        return this.filename;
    }

    public NodeSocket getSocket () {
        return this.socket;
    }

    public void addTransferListener (TransferListener listener) {
        this.listeners.add(listener);
    }

    public abstract void sendRequest(String filename) throws IOException;

    protected void notifyStart () {
        for (TransferListener listener : this.listeners) {
            listener.handleStart();
        }
    }

    protected void notifyMessage (Message msg) {
        for (TransferListener listener : this.listeners) {
            listener.handleMessage(msg);
        }
    }

    protected void notifyComplete () {
        for (TransferListener listener : this.listeners) {
            listener.handleComplete();
        }
    }

    public void logMessage(Message msg) {
        this.logger.info(msg.toString());
    }
}
