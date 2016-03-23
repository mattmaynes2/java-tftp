package stream;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Arrays;

import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageFactory;
import core.req.OpCode;

/**
 *  Stream that only counts the number of data packets received
 * @author Jeremy
 *
 */
public class CountDataPacketStream extends PacketStream {

    public CountDataPacketStream() throws SocketException {
        super();
    }

    /**
     * increment the number of packets received only if the packet is a data packet
     */
    @Override
    protected void incNumRecieved(DatagramPacket packet) {
        byte[] bytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        Message msg;
        try {
            msg = MessageFactory.createMessage(bytes);
            if(OpCode.DATA.equals(msg.getOpCode())) {
                super.incNumRecieved(packet);
            }
        } catch (InvalidMessageException e) {
            // don't increment count
        }

    }
}
