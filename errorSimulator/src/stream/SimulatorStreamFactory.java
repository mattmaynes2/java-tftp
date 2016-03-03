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
	private final static String REQ_STREAM_OPTION = "req";
	
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
            case REPLACE_PACKET:
                return new InjectPacketStream(createCountingStream((String)streamArgs[0]), (PacketModifier)streamArgs[2], (int) streamArgs[1]);
            case CHANGE_SENDER:
                return new WrongSenderStream(new PacketStream(), (int)streamArgs[0]);
            case DELAY_PACKET:
            	return new DelayedPacketStream(createCountingStream((String)streamArgs[0]), (int)streamArgs[1], (int)streamArgs[2]);
            case DUPLICATE_PACKET:
            	return new DuplicatePacketStream(createCountingStream((String)streamArgs[0]), (int)streamArgs[1]);
            case DROP_PACKET:
            	return new DropPacketStream(createCountingStream((String)streamArgs[0]), (int)streamArgs[1]);
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
    private static PacketStream createCountingStream(String dataOrAck) throws SocketException{
    	if (dataOrAck.toLowerCase().equals(DATA_STREAM_OPTION)){
    		return new CountDataPacketStream();
    	}else if (dataOrAck.toLowerCase().equals(ACK_STREAM_OPTION)){
    		return new CountAcksStream();
    	}else if (dataOrAck.toLowerCase().equals(REQ_STREAM_OPTION)){
    		PacketStream stream = new PacketStream();
    		stream.incNumRecieved(null);
    		return stream;
    	}else{
    		throw new IllegalArgumentException("Type of packet must be either 'data','ack', or 'req'");
    	}
    }
}
