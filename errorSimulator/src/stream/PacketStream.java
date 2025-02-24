package stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import core.util.ByteUtils;


public class PacketStream extends SimulatorStream {

	private static final Logger LOGGER = Logger.getGlobal();
    private DatagramSocket socket;
    private int numReceived;

    public PacketStream() throws SocketException {
        this.socket= new DatagramSocket();
        this.numReceived=0;
    }

    /**
     * receives a packet on a DatagramSocket
     * @throws IOException if stream closes during transfer
     */
    public DatagramPacket receive() throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
        socket.receive(packet);
        incNumRecieved(packet);
        return packet;
    }

    /**
     * Sends a packet using a DatagramSocket
     * @param packet {@link DatagramPacket} to send
     * @throws IOException if the socket is closed during transfer
     */
    public boolean send(DatagramPacket packet) throws IOException {
    	LOGGER.log(Level.INFO, "Sending from "+socket.getLocalSocketAddress());
        byte[] bytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        LOGGER.log(Level.INFO, "Bytes are: "+ByteUtils.bytesToHexString(bytes));
        LOGGER.log(Level.INFO,"Sending message to: "+packet.getSocketAddress());
        socket.send(packet);
        return true;
    }

    /**
     * @return the number of packets received
     */
    @Override
    public int getNumberPacketsOfPackets() {
        return numReceived;
    }
    /**
     * increment the number of packets received whenever a packet is received
     * @param packet the {@link DatagramPacket} that needs to be checked before incrementing the number of packets received
     */
    protected void incNumRecieved(DatagramPacket packet) {
        numReceived++;
    }

	@Override
	public void close() {
		socket.close();
	}

}
