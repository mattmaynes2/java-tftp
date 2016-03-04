package stream;

import java.io.IOException;
import core.log.Logger;

import java.util.Arrays;
import java.util.logging.Level;
import java.net.DatagramPacket;

import core.req.InvalidMessageException;

public class DuplicatePacketStream implements SimulatorStream {

	private PacketStream stream;
	int duplicatedPacketNumber;
	private boolean hasDuplicated;
	
	public DuplicatePacketStream(PacketStream stream, int duplicatedPacketNumber){
		this.duplicatedPacketNumber = duplicatedPacketNumber;
		this.stream = stream;
		this.hasDuplicated = false;
	}

	@Override
	public DatagramPacket receive() throws IOException {
		return this.stream.receive();
	}

	@Override
	public boolean send(DatagramPacket packet) throws IOException, InvalidMessageException {
		this.stream.send(packet);
		if (!hasDuplicated && (this.stream.getNumberPacketsOfPackets() == this.duplicatedPacketNumber)){
			Logger.log(Level.INFO, "Duplicating packet: " + Arrays.toString(packet.getData()));
			hasDuplicated = true;
			this.stream.send(packet);
		}
		return true;
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
