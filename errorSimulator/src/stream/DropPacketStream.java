package stream;

import java.io.IOException;
import core.log.Logger;

import java.util.Arrays;
import java.util.logging.Level;

import java.net.DatagramPacket;

import core.req.InvalidMessageException;

public class DropPacketStream implements SimulatorStream {

	private PacketStream stream;
	private int dropPacketNumber;
	private boolean hasDropped;
	
	public DropPacketStream(PacketStream stream, int dropPacketNumber){
		this.stream = stream;
		this.dropPacketNumber = dropPacketNumber;
		this.hasDropped = false;
	}

	@Override
	public DatagramPacket receive() throws IOException {
		return this.stream.receive();
	}

	@Override
	public void send(DatagramPacket packet) throws IOException, InvalidMessageException {
		if (hasDropped || this.stream.getNumberPacketsOfPackets() != this.dropPacketNumber){
			this.stream.send(packet);
		}else{
			hasDropped = true;
			Logger.log(Level.INFO, "dropped packet: " + Arrays.toString(packet.getData()));
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
