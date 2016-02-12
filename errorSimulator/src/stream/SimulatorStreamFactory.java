package stream;

import java.net.SocketException;
import sim.SimulationTypes;
import stream.InjectPacketStream;
import stream.PacketStream;
import stream.SimulatorStream;
import stream.WrongSenderStream;
import sim.PacketModifier;

public class SimulatorStreamFactory {

	/**
	 * Static method that creates a decorated SimulatorStream based on the input parameters
	 * @param type
	 * @param modifier
	 * @param packetToChange
	 * @return
	 * @throws SocketException
	 */
    public static SimulatorStream createSimulationStream(SimulationTypes type,PacketModifier modifier,int packetToChange) throws SocketException {
        switch(type) {
            case REPLACE_ACK:
                return new InjectPacketStream(new CountAcksStream(), modifier, packetToChange);
            case REPLACE_DATA:
                return new InjectPacketStream(new CountDataPacketStream(), modifier, packetToChange);
            case REPLACE_PACKET:
                return new InjectPacketStream(new PacketStream(), modifier, packetToChange);
            case CHANGE_SENDER:
                return new WrongSenderStream(new PacketStream(), packetToChange);
            default:
                return  new PacketStream();
        }
    }
}
