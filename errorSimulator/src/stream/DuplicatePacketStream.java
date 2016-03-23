package stream;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.DatagramPacket;

import threads.SimulatorThread;
import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageFactory;
import core.req.OpCode;

public class DuplicatePacketStream extends SimulatorStream {

    private static final Logger LOGGER = Logger.getGlobal();
    private PacketStream stream;
    private int duplicatedPacketNumber;
    private boolean hasDuplicated;

    public DuplicatePacketStream(PacketStream stream, int duplicatedPacketNumber){
        this.duplicatedPacketNumber = duplicatedPacketNumber;
        this.stream = stream;
        this.hasDuplicated = false;
    }

    @Override
    public DatagramPacket receive() throws IOException {
        return this.stream.receive();
    }

    @Override
    public boolean send(DatagramPacket packet) throws IOException, InvalidMessageException {
        this.stream.send(packet);
        if (!hasDuplicated && (this.stream.getNumberPacketsOfPackets() == this.duplicatedPacketNumber)){
            LOGGER.log(Level.INFO, "Duplicating packet: " + Arrays.toString(packet.getData()));
           
            Message msg=MessageFactory.createMessage(packet.getData());
            if (OpCode.READ.equals(msg.getOpCode())||OpCode.WRITE.equals(msg.getOpCode())) {
            	packet.setSocketAddress(getClientAddress()); 
            	// Spawn new thread for duplicate request
                (new Thread (new SimulatorThread(packet, new PacketStream()))).start();
            }else {
            	this.stream.send(packet);              
            }
            hasDuplicated = true;
        }
        return true;
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
