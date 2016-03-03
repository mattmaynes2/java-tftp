package stream;

import java.io.IOException;
import java.net.DatagramPacket;

import core.req.InvalidMessageException;

public class DuplicatePacketStream implements SimulatorStream {

	private PacketStream stream;
	int duplicatedPacketNumber;
	
	public DuplicatePacketStream(PacketStream stream, int duplicatedPacketNumber){
		this.duplicatedPacketNumber = duplicatedPacketNumber;
		this.stream = stream;
	}

	@Override
	public DatagramPacket receive() throws IOException {
		return this.stream.receive();
	}

	@Override
	public void send(DatagramPacket packet) throws IOException, InvalidMessageException {
		this.stream.send(packet);
		if (this.stream.getNumberPacketsOfPackets() == this.duplicatedPacketNumber){
			this.stream.send(packet);
		}
	}

	@Override
	public int getNumberPacketsOfPackets() {
		return this.stream.getNumberPacketsOfPackets();
	}

	@Override
	public void close() {
		this.stream.close();
	}
	

}
