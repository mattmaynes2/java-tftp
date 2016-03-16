package stream;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.DatagramPacket;

import core.req.InvalidMessageException;

public class DuplicatePacketStream implements SimulatorStream {
	private static final Logger LOGGER = Logger.getGlobal();
	private PacketStream stream;
	private int duplicatedPacketNumber;
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
			LOGGER.log(Level.INFO, "Duplicating packet: " + Arrays.toString(packet.getData()));
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
