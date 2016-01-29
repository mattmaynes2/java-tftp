package core.net;

import core.req.Message;
import core.req.MessageFactory;
import core.req.InvalidMessageException;

import java.util.Arrays;

import java.io.IOException;

import java.net.SocketAddress;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

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
     * Address of the socket that this socket is communicating with.
     */
    private SocketAddress address;

    /**
     * UDP socket that this socket is using for sending and receiving
     */
    private DatagramSocket socket;

    /**
     * Constructs a new node socket with a dynamically assigned port
     *
     * @throws SocketException - If there are no ports available
     */
    public NodeSocket () throws SocketException {
        this.socket = new DatagramSocket();
    }

    /**
     * Constructs a new node socket with a dynamically assigned port
     * that will communicate to the desired address
     *
     * @param address - Address to send messages to
     */
    public NodeSocket (SocketAddress address) throws SocketException {
        this.address = address;
        this.socket = new DatagramSocket();

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
    }

    /**
     * Returns the address of the endpoint that this socket is currently
     * communicating with
     *
     * @return Communication endpoint address
     */
    public SocketAddress getAddress(){
        return address;
    }

    /**
     * Sets the address that this socket should communicate with
     *
     * @param address - New socket endpoint address to communicate with
     */
    public void setAddress(SocketAddress address){
        this.address = address;
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
     */
    public Message receive() throws IOException, InvalidMessageException {
        // Create a packet to buffer the data received
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

        // Block and receive the packet
        this.socket.receive(packet);

        // The packet will contain an address of the sender. Capture that
        // address for future communication
        this.address = packet.getSocketAddress();

        return MessageFactory.createMessage(
                Arrays.copyOfRange(packet.getData(), 0, packet.getLength())
                );
    }

    /**
     * Closes this node socket and frees its port
     */
    public void close () {
        this.socket.close();
    }
}
