package stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import core.req.InvalidMessageException;

/**
 * Sends and Receives DatagramPackets and counts the number of packets received
 * @author Jeremy
 *
 */
public abstract class SimulatorStream {

    /**
     * Address of client
     */
    private SocketAddress clientAddress;

    /**
     * Sets the client's socket address for this stream
     *
     * @param address - Socket address of client
     */
    public void setClientAddress (SocketAddress address) {
        this.clientAddress = address;
    }

    /**
     * Returns the client's socket address for this stream
     */
    public SocketAddress getClientAddress () {
        return this.clientAddress;
    }

	/**
	 * Receive a DatagramPacket
	 * @return the DatagramPacket that is received the stream's socket
	 * @throws IOException - an IO Exception can occur if the socket becomes busy during a transfer
	 */
	public abstract DatagramPacket receive() throws IOException;

    /**
	 * Sends a DatagramPacket
	 * @param packet - the DatagramPacket that the stream sends
	 * @return If the packet was sent successfully
	 * @throws IOException  - an IO Exception can occur if the socket becomes busy during a transfer
	 * @throws InvalidMessageException - an Invalid Message Exception can occur if the sent packet is not in the proper format
	 */
	public abstract boolean send(DatagramPacket packet) throws IOException, InvalidMessageException;

	/**
	 * Get the number of packets that have been received
	 * @return The number of packets received
	 */
	public abstract int getNumberPacketsOfPackets();

	/**
	 * Close the stream
	 */
	public abstract void close();
}
