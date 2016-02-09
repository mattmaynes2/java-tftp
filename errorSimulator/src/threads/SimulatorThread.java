package threads;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;

import core.log.Logger;
import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageFactory;
import core.util.ByteUtils;

/**
 * Performs the communication between a client and a server once communication has been started
 *
 */
public abstract class SimulatorThread extends Thread {


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
	}

	/**
	 * Allows the thread to run.  Completes an entire transaction
	 */
	@Override
	public void run() {
		byte[] bytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
		Logger.log(Level.INFO,"Received Packet From "+packetIn.getSocketAddress());
		Logger.log(Level.INFO,"Bytes are: "+ByteUtils.bytesToHexString(bytes));
		try {
			Message msg=MessageFactory.createMessage(bytes);
			System.out.println(msg);
			sendPacket(msg);
			//Loops until it is about to receive the last message
			handleMessage(msg);

		} catch (IOException | InvalidMessageException e) {
			e.printStackTrace();
		}
	}

	abstract void handleMessage(Message msg) throws IOException, InvalidMessageException;

	/**
	 * Receives a packet and returns it as a Message
	 * @return Message created from received packet
	 * @throws IOException
	 * @throws InvalidMessageException
	 */
	protected Message receivePacket() throws IOException, InvalidMessageException {
		socket.receive(packetIn);
		byte[] bytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
		Logger.log(Level.INFO,"Received Packet From "+packetIn.getSocketAddress());
		Logger.log(Level.INFO,"Bytes are: "+ByteUtils.bytesToHexString(bytes));
		return MessageFactory.createMessage(bytes);
	}

	/**
	 * Takes a message and uses it to create and send a packet
	 * @param message
	 * @throws IOException
	 */
	protected void sendPacket(Message message) throws IOException {
		Logger.log(Level.INFO,"Sending message to: "+sendAddress);
		Logger.log(Level.INFO,"Message is: "+message);
		Logger.log(Level.INFO,"Bytes are: "+ByteUtils.bytesToHexString(message.toBytes()));
		socket.send(new DatagramPacket(message.toBytes(), message.toBytes().length,sendAddress));
		sendAddress=packetIn.getSocketAddress();
		Logger.log(Level.INFO,"Set next address to send "+sendAddress);
	}
}