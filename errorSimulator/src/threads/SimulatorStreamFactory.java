package threads;

import java.net.SocketException;

import sim.PacketModifier;

public class SimulatorStreamFactory {

	public static SimulatorStream createSimulationStream(SimulationTypes type,PacketModifier modifier,int packetToChange) throws SocketException {
		switch(type) {
		case REPLACE_PACKET:
			return new InjectPacketStream(new PacketStream(), modifier, packetToChange);
		case CHANGE_SENDER:
			return new WrongSenderStream(new PacketStream(), packetToChange);
		default:
				return  new PacketStream();
		}
	}
}
