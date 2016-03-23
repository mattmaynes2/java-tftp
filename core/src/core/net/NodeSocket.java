package core.net;

import core.req.Message;
import core.req.MessageFactory;
import core.req.InvalidMessageException;
import core.req.ErrorCode;
import core.req.ErrorMessage;

import java.util.ArrayList;
import java.util.Arrays;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * NodeSocket
 *
 * A high level UDP socket that sends and receives Messages.
 * Sockets can be bound to specific port and listen for message or can be
 * dynamically assigned. The socket contains an address field that will
 * be the recipient of any messages sent. When a message is received on
 * the socket the address will be updated to be the sender's address.
 */
public class NodeSocket {

    /**
	 * Timeout time for the socket in ms
	 */
    private static final int TIMEOUT_TIME = 2400;

    /**
     * Default number of attempts to retry sending and receiving after timeouts
     */
    private static final int DEFAULT_ATTEMPTS = 5;

	/**
     * Address of the socket that this socket is communicating with.
     */
    private SocketAddress address;

    /**
     * Number of retry attempts when a socket times out
     */
    private int attempts;

    /**
     * UDP socket that this socket is using for sending and receiving
     */
    private DatagramSocket socket;

    /**
     * Listeners to events of this socket
     */
    private ArrayList<NodeSocketListener> listeners;

    /**
     * Constructs a new node socket with a dynamically assigned port
     *
     * @throws SocketException - If there are no ports available
     */
    public NodeSocket () throws SocketException {
        this.listeners  = new ArrayList<NodeSocketListener>();
        this.socket     = new DatagramSocket();
        this.attempts   = DEFAULT_ATTEMPTS;
        // Set the socket timeout
        socket.setSoTimeout(TIMEOUT_TIME);
    }

    /**
     * Constructs a new node socket with a dynamically assigned port
     * that will communicate to the desired address
     *
     * @param address - Address to send messages to
     *
     * @throws SocketException - If there are no ports available
     */
    public NodeSocket (SocketAddress address) throws SocketException {
        this.socket     = new DatagramSocket();
        this.listeners  = new ArrayList<NodeSocketListener>();
        this.address    = address;
        this.attempts   = DEFAULT_ATTEMPTS;

        // Set timeout
        socket.setSoTimeout(TIMEOUT_TIME);
    }

    /**
     * Constructs a new node socket bound to the desired port
     *
     * @param port - Port to bind this socket to
     *
     * @throws SocketException - If the port is in use or there are insufficient privileges
     */
    public NodeSocket (int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.attempts = DEFAULT_ATTEMPTS;
    }

    /**
     * Returns the address of the endpoint that this socket is currently
     * communicating with
     *
     * @return Communication endpoint address
     */
    public SocketAddress getAddress(){
        return this.address;
    }

    /**
     * Sets the address that this socket should communicate with
     *
     * @param address - New socket endpoint address to communicate with
     */
    public void setAddress (SocketAddress address){
        this.address = address;
    }

    /**
     * Adds a listener for the transfer events
     *
     * @param listener - The listener to add
     */
    public void addNodeSocketListener (NodeSocketListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Sets the number of attempts that a socket should try to send or
     * receive on a socket after timeouts
     *
     * @param attempts - Number of retry attempts
     */
    public void setAttempts (int attempts) {
        this.attempts = attempts;
    }

    /**
     * Resets the socket to its initial state
     */
    public void reset () {
        this.address = null;
    }

    /**
     * Sends a message to this socket's current endpoint
     *
     * @param message - Message to send on socket
     *
     * @throws IOException - If the socket is closed or the endpoint cannot be reached
     */
    public void send (Message message) throws IOException {
        this.send(message, this.address);
    }

    /**
     * Sends a message to a specific address once, this does not set
     * the socket's address to be the new endpoint
     *
     * @param message - Message to send
     * @param address - Endpoint to send message
     *
     * @throws IOException - If the socket is closed or the endpoint cannot be reached
     */
    public void send (Message message, SocketAddress address) throws IOException {
        byte[] data = message.toBytes();
        this.socket.send(new DatagramPacket(data, data.length, address));
    }

    /**
     * Synchronously blocks and waits for a message on the socket and updates
     * the socket's address to be the sender's address
     *
     * @return The received message
     *
     * @throws IOException - If the transmission fails
     * @throws InvalidMessageException - If the message received is not valid
     * @throws UnreachableHostException - If the host is no longer listening
     */
    public Message receive() throws
        IOException, InvalidMessageException, UnreachableHostException {

        // Create a packet to buffer the data received
        DatagramPacket packet;
        boolean received;
        int attempt = 0;

        do {
            // Receive the packets from the socket
            packet = new DatagramPacket(new byte[1024], 1024);
            received=false;
            while (!received) {
                try {
                    this.socket.receive(packet);
                    received = true;
                } catch (SocketTimeoutException e) {
                    // Increment the attempt count and heck if the maximum number
                    // of timeouts have been reached.
                    if (++attempt == this.attempts) {
                        throw new UnreachableHostException("No response from host after "
                            + this.attempts + " attempts");
                    }
                    this.notifyTimeout(this.attempts - attempt);
                }
            }
        }
        // If the sender is invalid then receive another packet
        while(!this.validateEndpoint(packet));

        // The packet will contain an address of the sender. Capture that
        // address for future communication
        this.address = packet.getSocketAddress();

        return MessageFactory.createMessage(
            Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));
    }

    /**
     * Closes this node socket and frees its port
     */
    public void close () {
        this.socket.close();
    }

    /**
     * Checks that the given packet is from the correct endpoint
     *
     * @param packet - Packet to confirm endpoint
     */
    private boolean validateEndpoint (DatagramPacket packet) throws IOException {
        if (this.address != null && !this.address.equals(packet.getSocketAddress())) {
            this.notifyUnknownTID();
            this.send(
                new ErrorMessage(ErrorCode.UNKNOWN_TID, "Unknown transfer ID"),
                packet.getSocketAddress());
            return false;
        }
        return true;
    }

    /**
     * Notifies that a timeout occurred and how many timeouts are remaining
     */
    private void notifyTimeout (int remaining) {
        for (NodeSocketListener l : this.listeners) {
            l.handleTimeout(remaining);
        }
    }

    /**
     * Notifies all listeners that a message has been received
     * from an unknown TID
     */
    private void notifyUnknownTID () {
        for (NodeSocketListener l : this.listeners) {
            l.handleUnknownTID();
        }
    }
}
