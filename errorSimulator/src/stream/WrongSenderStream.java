package stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.Level;

import core.log.Logger;
import core.req.InvalidMessageException;

public class WrongSenderStream implements SimulatorStream {

    private SimulatorStream mainStream;
    private PacketStream wrongStream;
    private int sendAt;

    public WrongSenderStream(SimulatorStream stream, int sendAt) throws SocketException {
        this.mainStream=stream;
        this.wrongStream= new PacketStream();
        this.sendAt=sendAt;
    }

    @Override
    public DatagramPacket receive() throws IOException {
        return mainStream.receive();
    }

    @Override
    public void send(DatagramPacket packet) throws IOException, InvalidMessageException {
        if(getNumberPacketsOfPackets()==sendAt) {
            // send from the wrong socket
            Logger.log(Level.INFO,"Sending packet from wrong stream");
            wrongStream.send(packet);
            DatagramPacket responsePacket=wrongStream.receive();
            byte[] bytes = Arrays.copyOfRange(responsePacket.getData(), 0, responsePacket.getLength());
            Logger.log(Level.INFO,"Received Packet From "+responsePacket.getSocketAddress());
            Logger.log(Level.INFO, "Sending original packet from right stream");
            mainStream.send(packet);
        }else {
            mainStream.send(packet);
        }

    }

    @Override
    public int getNumberPacketsOfPackets() {
        return mainStream.getNumberPacketsOfPackets();
    }

}
