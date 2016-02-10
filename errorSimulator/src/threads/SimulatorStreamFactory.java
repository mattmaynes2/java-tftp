package threads;

import java.net.DatagramPacket;
import java.net.SocketException;

public class SimulatorStreamFactory {

	public static SimulatorStream createSimulationStream(SimulationTypes type,DatagramPacket packet,int packetToChange) throws SocketException {
		switch(type) {
		case REPLACE_PACKET:
			return new InjectPacketStream(new PacketStream(), packet, packetToChange);
		case CHANGE_SENDER:
			return new WrongSenderStream(new PacketStream(), packetToChange);
		default:
				return  new PacketStream();
		}
	}
}
