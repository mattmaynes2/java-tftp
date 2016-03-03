package stream;

import java.io.IOException;
import java.net.DatagramPacket;

import core.req.InvalidMessageException;

/**
 * DelayedPacketStream
 * Will delay a specific packet send on an underlying stream
 */
public class DelayedPacketStream implements SimulatorStream{
	
	PacketStream stream;
	int delayedPacketNumber;
	int delayTime;
	boolean alreadyDelayed = false;
	
	/**
	 * 
	 * @param stream A PacketStream to delay packets for
	 */
	public DelayedPacketStream(PacketStream stream, int delayedPacketNumber, int timeoutMilliseconds){
		this.stream = stream;
		this.delayedPacketNumber = delayedPacketNumber;
		this.delayTime = timeoutMilliseconds;
	}
	
	@Override
	public DatagramPacket receive() throws IOException {
		return this.stream.receive();
	}

	@Override
	public boolean send(DatagramPacket packet) throws IOException, InvalidMessageException {
		if ((stream.getNumberPacketsOfPackets() == delayedPacketNumber) && !alreadyDelayed){
			try {
				Thread.sleep(this.delayTime);
				alreadyDelayed = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.stream.send(packet);
		return true;
	}

	@Override
	public int getNumberPacketsOfPackets() {
		return stream.getNumberPacketsOfPackets();
	}

	@Override
	public void close() {
		stream.close();
	}
		
}
