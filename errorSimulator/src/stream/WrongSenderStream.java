package stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.Level;

import core.log.Logger;
import core.req.InvalidMessageException;
import core.util.ByteUtils;

/**
 * Sends a packet on a different socket than the receiver is expecting
 * used to cause error code 5s 
 * @author Jeremy
 *
 */
public class WrongSenderStream implements SimulatorStream {

    private SimulatorStream mainStream;
    private PacketStream wrongStream;
    private int sendAt;
    private boolean hasSentFromWrongStream;
    
    /**
     * 
     * @param stream the stream that will be used for main communication
     * @param sendAt packet number in sequence that will trigger a packet to be sent from the wrong stream
     * @throws SocketException thrown if there are no available ports
     */
    public WrongSenderStream(SimulatorStream stream, int sendAt) throws SocketException {
        this.mainStream=stream;
        this.wrongStream= new PacketStream();
        this.sendAt=sendAt;
        this.hasSentFromWrongStream = false;
    }

    @Override
    public DatagramPacket receive() throws IOException {
        return mainStream.receive();
    }

   /**
    * If the number of packets received is equal to the packet in the sequence to modify
    *  Send a packet out from a stream using a different address than the one that had been used
    *  for previous communication. 
    *  otherwise send packet from the main stream used for communication
    */
    @Override
    public void send(DatagramPacket packet) throws IOException, InvalidMessageException {
        if(!hasSentFromWrongStream && getNumberPacketsOfPackets()==sendAt) {
            // send from the wrong socket
        	hasSentFromWrongStream = true;
            Logger.log(Level.INFO, "Sending original packet from right stream");
            mainStream.send(packet);
            Logger.log(Level.INFO,"Sending packet from wrong stream");
            wrongStream.send(packet);
            DatagramPacket responsePacket=wrongStream.receive();
            byte[] bytes = Arrays.copyOfRange(responsePacket.getData(), 0, responsePacket.getLength());
            Logger.log(Level.INFO,"Received Packet From "+responsePacket.getSocketAddress());
            Logger.log(Level.INFO, "Bytes are: "+ByteUtils.bytesToHexString(bytes));
        }else {
            mainStream.send(packet);
        }

    }

    /**
     * returns the number of packets received by the main stream used for communication
     */
    @Override
    public int getNumberPacketsOfPackets() {
        return mainStream.getNumberPacketsOfPackets();
    }

    /**
     * close the stream
     */
	@Override
	public void close() {
		mainStream.close();
	}

}
