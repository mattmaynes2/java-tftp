package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class PacketStream implements SimulatorStream{

	private DatagramSocket socket;
	
	
	public PacketStream() throws SocketException {
		this.socket= new DatagramSocket();
	}
	
	public DatagramPacket receive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
		socket.receive(packet);
		return packet;
	}
	
	
	public void send(DatagramPacket packet) throws IOException {
		socket.send(packet);
	}
	

}
