package stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.logging.Level;
import java.util.logging.Logger;
import core.req.InvalidMessageException;
import sim.PacketModifier;

public class InjectPacketStream extends SimulatorStream {

	private static final Logger LOGGER = Logger.getGlobal();
    private PacketStream stream;
    private PacketModifier modifier;
    private int injectAt;
    private boolean hasInjected;

/**
 *
 * @param stream a Packet stream that sends and receives packets
 * @param modifier modifier object used to modify the packet before sending out
 * @param injectAt the packet number in the sequence to replace with the modified packet
 */
    public InjectPacketStream(PacketStream stream,PacketModifier modifier,int injectAt) {
        this.stream=stream;
        this.modifier=modifier;
        this.injectAt=injectAt;
        this.hasInjected = false;
    }


    @Override
    public DatagramPacket receive() throws IOException {
        return stream.receive();
    }

    /**
     * Replaces the packet at the specified point in the sequence with the modified packet
     */
    @Override
    public boolean send(DatagramPacket packet) throws IOException, InvalidMessageException {
        if(!hasInjected && getNumberPacketsOfPackets()==injectAt) {
        	hasInjected = true;
        	LOGGER.log(Level.INFO, "About to Send Modifyied Packet");
        	stream.send(modifier.modifyPacket(packet));
        }else {
            stream.send(packet);
        }
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
