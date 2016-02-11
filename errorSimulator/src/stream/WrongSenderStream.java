package stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import core.req.InvalidMessageException;

public class WrongSenderStream implements SimulatorStream {

	private SimulatorStream stream;
	private DatagramSocket socket;
	private int sendAt;
	
	public WrongSenderStream(SimulatorStream stream, int sendAt) throws SocketException {
		this.stream=stream;
		this.socket= new DatagramSocket();
	}
	
	@Override
	public DatagramPacket receive() throws IOException {
		return stream.receive();
	}

	@Override
	public void send(DatagramPacket packet) throws IOException, InvalidMessageException {
		if(getNumberPacketsOfPackets()==sendAt) {
			// send from the wrong socket
			socket.send(packet);
			stream.send(packet);
		}else {
			stream.send(packet);
		}

	}

	@Override
	public int getNumberPacketsOfPackets() {
		return stream.getNumberPacketsOfPackets();
	}

}
