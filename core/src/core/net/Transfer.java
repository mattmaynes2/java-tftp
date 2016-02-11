package core.net;

import core.net.NodeSocket;
import core.net.TransferListener;

import core.req.Message;
import core.req.OpCode;
import core.req.AckMessage;
import core.req.ErrorCode;
import core.req.ErrorMessage;
import core.req.InvalidMessageException;
import core.req.ErrorMessageException;
import core.req.MessageOrderException;

import java.io.IOException;
import java.util.ArrayList;

import java.net.SocketAddress;
import java.net.SocketException;

/**
 * Transfer
 *
 * Runnable transfer moves a file between UDP endpoints.
 * It is left to the subclass to use the appropriate notification function to
 * signal transfer listeners of the different stages of the transfer.
 */
public abstract class Transfer implements Runnable {

    /**
     * Listeners to the stages of a transfer
     */
    private ArrayList<TransferListener> listeners;

    /**
     * Socket that will be used to perform this transfer. This socket
     * will be closed once the transfer is complete
     */
    private NodeSocket socket;

    /**
     * Name of the file to transfer
     */
    private String filename;

    /**
     * Current data block being transferred
     */
    private short currentBlock;

    /**
     * Constructs a transfer with a socket which will move the specified file
     *
     * @param address - Address to use as the endpoint
     * @param filename - Path of file to transfer
     *
     * @throws SocketException - If the socket cannot be created
     */
    public Transfer (SocketAddress address, String filename) throws SocketException {
        this.filename = filename;
        this.socket = new NodeSocket(address);
        this.listeners = new ArrayList<TransferListener>();
        this.currentBlock = 0;
    }

    protected void handleInvalidMessage (InvalidMessageException error) {
        try {
            // TODO Log Error
            this.socket.send(
                new ErrorMessage(ErrorCode.ILLEGAL_OP, error.getMessage()));
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected void checkErrorMessage (Message msg) throws ErrorMessageException {
        if (msg.getOpCode() == OpCode.ERROR) {
            // TODO Log Message
            this.notifyError((ErrorMessage) msg);
            throw new ErrorMessageException((ErrorMessage) msg);
        }
    }

    protected void checkOrder (AckMessage ack) throws MessageOrderException {
       if (ack.getBlock() != this.getBlockNumber()) {
            throw new MessageOrderException(
                ack.getOpCode() + " Message out of order." +
                " Expected " + this.getBlockNumber() +
                " Received " + ack.getBlock());
        }
    }

    protected boolean checkCast (Message msg, OpCode code) throws InvalidMessageException {
        if (msg.getOpCode() == code){
            return true;
        }
        throw new InvalidMessageException("Invalid message in stream");
    }

    /**
     * Returns the name of the file that is being transfered
     *
     * @return Name of file being transfered
     */
    public String getFilename () {
        return this.filename;
    }

    /**
     * Returns the socket being used for this transfer
     *
     * @return The socket being used for this transfer
     */
    public NodeSocket getSocket () {
        return this.socket;
    }

    /**
     * Adds a listener to this transfer
     *
     * @param listener - Listener to add to this transfer
     */
    public void addTransferListener (TransferListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Sends the initializing request to start this request
     */
    public abstract void sendRequest() throws IOException;

    /**
     * Returns the block number that this transfer is currently processing
     *
     * @return Current block number index
     */
    public short getBlockNumber() {
        return this.currentBlock;
    }

    /**
     * Increments the current block number and then returns the new value
     *
     * @return The next block number
     */
    protected short incrementBlockNumber () {
        return this.currentBlock++;
    }

    /**
     * Notifies all listeners that the transfer has started
     */
    protected void notifyStart () {
        for (TransferListener listener : this.listeners) {
            listener.handleStart();
        }
    }

    /**
     * Notifies all listeners that a message has been received
     *
     * @param msg - Message that was received
     */
    protected void notifyMessage (Message msg) {
        for (TransferListener listener : this.listeners) {
            listener.handleMessage(msg);
        }
    }

    /**
     * Notifies all listeners that a message is being sent
     * @param msg - Message being sent
     */
    protected void notifySendMessage(Message msg){
        for (TransferListener listener : this.listeners) {
            listener.handleSendMessage(msg);
        }
    }

    protected void notifyError (ErrorMessage msg) {
        for (TransferListener listener : this.listeners) {
            listener.handleErrorMessage(msg);
        }
    }

    /**
     * Notifies all listeners that the transfer has completed
     */
    protected void notifyComplete () {
        for (TransferListener listener : this.listeners) {
            listener.handleComplete();
        }
    }

}
