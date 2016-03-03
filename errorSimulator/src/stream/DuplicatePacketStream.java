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
	int timeout;
	private boolean hasDuplicated;
	
	public DuplicatePacketStream(PacketStream stream, int duplicatedPacketNumber, int timeout){
		this.duplicatedPacketNumber = duplicatedPacketNumber;
		this.stream = stream;
		this.hasDuplicated = false;
		this.timeout = timeout;
	}

	@Override
	public DatagramPacket receive() throws IOException {
		return this.stream.receive();
	}

	@Override
	public void send(DatagramPacket packet) throws IOException, InvalidMessageException {
		this.stream.send(packet);
		if (!hasDuplicated && (this.stream.getNumberPacketsOfPackets() == this.duplicatedPacketNumber)){
			try {
				Logger.log(Level.INFO, "Duplicating a packet after " + timeout + "seconds");
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Logger.log(Level.INFO, "Duplicating packet: " + Arrays.toString(packet.getData()));
			hasDuplicated = true;
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
