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
	 * @return
	 * @throws IOException
	 */
	public DatagramPacket receive() throws IOException;
	/**
	 * sends a DatagramPacket
	 * @param packet
	 * @throws IOException
	 * @throws InvalidMessageException
	 */
	public void send(DatagramPacket packet) throws IOException, InvalidMessageException;
	
	/**
	 * 
	 * @return The number of packets received
	 */
	public int  getNumberPacketsOfPackets();
}
