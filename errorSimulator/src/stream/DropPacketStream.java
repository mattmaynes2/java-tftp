package stream;

import java.io.IOException;
import java.net.DatagramPacket;

import core.req.InvalidMessageException;

public class DropPacketStream implements SimulatorStream {

	private PacketStream stream;
	private int dropPacketNumber;
	
	public DropPacketStream(PacketStream stream, int dropPacketNumber){
		this.stream = stream;
		this.dropPacketNumber = dropPacketNumber;
	}

	@Override
	public DatagramPacket receive() throws IOException {
		return this.stream.receive();
	}

	@Override
	public void send(DatagramPacket packet) throws IOException, InvalidMessageException {
		if (this.stream.getNumberPacketsOfPackets() != this.dropPacketNumber){
			this.stream.send(packet);
		}
	}

	@Override
	public int getNumberPacketsOfPackets() {
		return this.stream.getNumberPacketsOfPackets();
	}
	
	
}
