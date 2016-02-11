package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;

import core.log.Logger;
import core.util.ByteUtils;


public class PacketStream implements SimulatorStream{

	private DatagramSocket socket;
	private int numReceived;
	
	public PacketStream() throws SocketException {
		this.socket= new DatagramSocket();
		this.numReceived=1;
	}
	
	public DatagramPacket receive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
		socket.receive(packet);
		numReceived++;
		return packet;
	}
	
	
	public void send(DatagramPacket packet) throws IOException {
		Logger.log(Level.INFO,"Sending message to: "+packet.getSocketAddress());
		Logger.log(Level.INFO,"Bytes are: "+ByteUtils.bytesToHexString(packet.getData()));
		socket.send(packet);
	}

	@Override
	public int getNumberPacketsOfPackets() {
		return numReceived;
	}
	

}
