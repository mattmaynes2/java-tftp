package stream;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Arrays;
import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageFactory;
import core.req.OpCode;

/**
 * Stream that only counts the number of ack packets received
 * @author Jeremy
 *
 */
public class CountAcksStream extends PacketStream {

	public CountAcksStream() throws SocketException {
		super();
	}

	/**
	 * increments the number of packets received only if the packet is an ack
	 */
	@Override
	protected void incNumRecieved(DatagramPacket packet) {
		byte[] bytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
		Message msg;
		try {
			msg = MessageFactory.createMessage(bytes);
			if(OpCode.ACK.equals(msg.getOpCode())) {
				super.incNumRecieved(packet);
			}
		} catch (InvalidMessageException e) {
			// don't increment count
		}
		
	}

}
