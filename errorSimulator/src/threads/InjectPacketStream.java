package threads;

import java.io.IOException;
import java.net.DatagramPacket;

public class InjectPacketStream implements SimulatorStream {

	private PacketStream stream;
	private DatagramPacket toInject;
	private int injectAt;
	
	
	public InjectPacketStream(PacketStream stream,DatagramPacket toInject,int injectAt) {
		this.stream=stream;
		this.toInject=toInject;
		this.injectAt=injectAt;
	}
	
	@Override
	public DatagramPacket receive() throws IOException {
		return stream.receive();
	}

	@Override
	public void send(DatagramPacket packet) throws IOException {
		if(getNumberPacketsOfPackets()==injectAt) {
			toInject.setSocketAddress(packet.getSocketAddress());
			stream.send(toInject);
		}else {
			stream.send(packet);
		}

	}

	@Override
	public int getNumberPacketsOfPackets() {
		return stream.getNumberPacketsOfPackets();
	}

}
