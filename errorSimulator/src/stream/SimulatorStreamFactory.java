package stream;

import java.net.SocketException;
import sim.SimulationTypes;
import stream.InjectPacketStream;
import stream.PacketStream;
import stream.SimulatorStream;
import stream.WrongSenderStream;
import sim.PacketModifier;

public class SimulatorStreamFactory {

	private final static String DATA_STREAM_OPTION = "data";
	private final static String ACK_STREAM_OPTION = "ack";
	
	/**
	 * Static method that creates a decorated SimulatorStream based on the input parameters
	 * @param type type of SimulatorStream to create
	 * @param modifier used to modify the packet
	 * @param packetToChange the number of the packet in the sequence that is going to be modified
	 * @return a decorated SimulatorStream
	 * @throws SocketException if no more sockets are available when creating a stream
	 */
    public static SimulatorStream createSimulationStream(SimulationTypes type, Object...streamArgs) throws SocketException {
        switch(type) {
            case REPLACE_ACK:
                return new InjectPacketStream(new CountAcksStream(), (PacketModifier)streamArgs[1], (int) streamArgs[0]);
            case REPLACE_DATA:
                return new InjectPacketStream(new CountDataPacketStream(), (PacketModifier)streamArgs[1], (int) streamArgs[0]);
            case REPLACE_PACKET:
                return new InjectPacketStream(new PacketStream(), (PacketModifier)streamArgs[0], (int) streamArgs[1]);
            case CHANGE_SENDER:
                return new WrongSenderStream(new PacketStream(), (int)streamArgs[1]);
            case DELAY_PACKET:
            	return new DelayedPacketStream(createAckOrDataStream((String)streamArgs[0]), (int)streamArgs[1], (int)streamArgs[2]);
            case DUPLICATE_PACKET:
            	return new DuplicatePacketStream(createAckOrDataStream((String)streamArgs[0]), (int)streamArgs[1]);
            case DROP_PACKET:
            	return new DropPacketStream(createAckOrDataStream((String)streamArgs[0]), (int)streamArgs[1]);
            default:
                return  new PacketStream();
        }
    }
    
    /**
     * 
     * @param dataOrAck Either "data" or "ack", specifies the stream type to create
     * @throws IllegalArgumentException if dataOrAck is not "data" or "ack"
     * @return The stream that is appropriate based on dataOrAck
     */
    private static PacketStream createAckOrDataStream(String dataOrAck) throws SocketException{
    	if (dataOrAck.toLowerCase().equals(DATA_STREAM_OPTION)){
    		return new CountDataPacketStream();
    	}else if (dataOrAck.toLowerCase().equals(ACK_STREAM_OPTION)){
    		return new CountAcksStream();
    	}else{
    		throw new IllegalArgumentException("Type of packet to count must be either 'data' or 'ack'");
    	}
    }
}
