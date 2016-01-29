import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageFactory;
import core.util.ByteUtils;

/**
 * Performs the communication between a client and a server once communication has been started
 *
 */
public class SimulatorThread extends Thread {


	private DatagramSocket socket;
	private DatagramPacket packetIn;
	private SocketAddress sendAddress;
	
	/**
	 * Creates a new socket and sets the timeout to 1000
	 * @param packet  the datagram packet
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public SimulatorThread(DatagramPacket packet) throws SocketException, UnknownHostException {
		this.packetIn=packet;
		this.sendAddress= new InetSocketAddress(InetAddress.getLocalHost(),69);
		socket= new DatagramSocket();
		socket.setSoTimeout(1000);
	}
	
	/**
	 * Allows the thread to run.  Completes an entire transaction
	 */
	@Override
	public void run() {
		byte[] bytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
		System.out.println("Received Packet From "+packetIn.getSocketAddress()); 
		System.out.println("Bytes are: "+ByteUtils.bytesToHexString(bytes));
		try {
			Message msg=MessageFactory.createMessage(bytes);
			System.out.println(msg);
			sendPacket(msg);
			//Loops until it is about to receive the last message
			while(!MessageFactory.isLastMessage(msg)) {
				msg=receivePacket();
				System.out.println("Message is "+msg);
				sendPacket(msg);
			}
			
			//Receives the last packet 
			msg=receivePacket();
			System.out.println("Message is "+msg);
			sendPacket(msg);
			
		} catch (IOException | InvalidMessageException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Receives a packet and returns it as a Message
	 * @return Message created from received packet
	 * @throws IOException
	 * @throws InvalidMessageException
	 */
	private Message receivePacket() throws IOException, InvalidMessageException {
		socket.receive(packetIn);
		byte[] bytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
		System.out.println("Received Packet From "+packetIn.getSocketAddress()); 
		System.out.print("Bytes are: "+ByteUtils.bytesToHexString(packetIn.getData()));
		return MessageFactory.createMessage(bytes);
	}
	
	/**
	 * Takes a message and uses it to create and send a packet
	 * @param message
	 * @throws IOException
	 */
	private void sendPacket(Message message) throws IOException {
		socket.send(new DatagramPacket(message.toBytes(), message.toBytes().length,sendAddress));
		sendAddress=packetIn.getSocketAddress();
		System.out.println("Set next address to send "+sendAddress);
	}
}
