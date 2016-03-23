package stream;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.DatagramPacket;

import core.req.InvalidMessageException;

public class DropPacketStream extends SimulatorStream {

    private static final Logger LOGGER = Logger.getGlobal();
    private PacketStream stream;
    private int dropPacketNumber;
    private boolean hasDropped;

    public DropPacketStream(PacketStream stream, int dropPacketNumber){
        this.stream = stream;
        this.dropPacketNumber = dropPacketNumber;
        this.hasDropped = false;
    }

    @Override
    public DatagramPacket receive() throws IOException {
        return this.stream.receive();
    }

    @Override
    public boolean send(DatagramPacket packet) throws IOException, InvalidMessageException {
        boolean dropped = false;
        if (hasDropped || this.stream.getNumberPacketsOfPackets() != this.dropPacketNumber){
            this.stream.send(packet);
        }else{
            hasDropped = true;
            dropped = true;
            LOGGER.log(Level.INFO, "dropped packet: " + Arrays.toString(packet.getData()));
        }
        return !dropped;
    }

    @Override
    public int getNumberPacketsOfPackets() {
        return this.stream.getNumberPacketsOfPackets();
    }

    @Override
    public void close() {
        this.stream.close();
    }


}
