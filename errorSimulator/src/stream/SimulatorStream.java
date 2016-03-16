package stream;

import java.io.IOException;
import java.net.DatagramPacket;

import core.req.InvalidMessageException;

/**
 * Sends and Receives DatagramPackets and counts the number of packets received
 * @author Jeremy
 *
 */
public interface SimulatorStream {

	/**
	 * Receive a DatagramPacket 
	 * @return the DatagramPacket that is received the stream's socket
	 * @throws IOException - an IO Exception can occur if the socket becomes busy during a transfer
	 */
	public DatagramPacket receive() throws IOException;
	/**
	 * Sends a DatagramPacket
	 * @param packet - the DatagramPacket that the stream sends
	 * @return TODO
	 * @throws IOException  - an IO Exception can occur if the socket becomes busy during a transfer
	 * @throws InvalidMessageException - an Invalid Message Exception can occur if the sent packet is not in the proper format
	 */
	public boolean send(DatagramPacket packet) throws IOException, InvalidMessageException;
	
	/**
	 * Get the number of packets that have been received
	 * @return The number of packets received
	 */
	public int  getNumberPacketsOfPackets();
	
	/**
	 * Close the stream
	 */
	public void close();
}
