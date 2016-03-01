package core.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import core.req.AckMessage;
import core.req.ErrorCode;
import core.req.ErrorMessage;
import core.req.ErrorMessageException;
import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageOrderException;
import core.req.OpCode;

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

    /**
     * Handles an invalid message exception by sending an error message on the
     * socket and notifies listeners of the invalid message
     *
     * @param error - Invalid message exception
     */
    protected void handleInvalidMessage (InvalidMessageException error) {
        ErrorMessage msg;
        try {
            msg = new ErrorMessage(ErrorCode.ILLEGAL_OP, error.getMessage());
            this.socket.send(msg);
            this.notifyError(msg);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Checks if the given message is an error message and throws an exception
     * if there is
     *
     * @param msg - Message to check
     *
     * @throws ErrorMessageException If the message is an error message
     */
    protected void checkErrorMessage (Message msg) throws ErrorMessageException {
        if (msg.getOpCode() == OpCode.ERROR) {
            throw new ErrorMessageException((ErrorMessage) msg);
        }
    }

    /**
     * Checks if the given acknowledge message is in the correct order
     * and throws an error if not
     *
     * @param ack - Acknowledge message
     *
     * @throws MessageOrderException - If the acknowledge is out of order
     */
    protected void checkOrder (AckMessage ack) throws MessageOrderException {
       if (ack.getBlock() != this.getBlockNumber()) {
            throw new MessageOrderException(
                ack.getOpCode() + " Message out of order." +
                " Expected " + this.getBlockNumber() +
                " Received " + ack.getBlock());
        }
    }

    /**
     * Checks if the given message has the correct op code to cast to the
     * desired message type.
     *
     * @param msg   - Message to check
     * @param code  - Opcode of message type to cast to
     *
     * @return If the cast can be performed
     *
     * @throws InvalidMessageException - If the message is not of the correct type
     */
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
     *
     * @return If the request was accepted
     *
     * @throws IOException - If socket is closed or there is no endpoint
     */
    public abstract boolean sendRequest() throws IOException;

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
     *
     * @param msg - Message being sent
     */
    protected void notifySendMessage(Message msg){
        for (TransferListener listener : this.listeners) {
            listener.handleSendMessage(msg);
        }
    }

    /**
     * Notifies all listeners that an error occurred
     *
     * @param msg - Error message that was received
     */
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
