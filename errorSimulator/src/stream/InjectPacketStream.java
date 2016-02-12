package stream;

import java.io.IOException;
import java.net.DatagramPacket;

import core.req.InvalidMessageException;
import sim.PacketModifier;

public class InjectPacketStream implements SimulatorStream {

    private PacketStream stream;
    private PacketModifier modifier;
    private int injectAt;


    public InjectPacketStream(PacketStream stream,PacketModifier modifier,int injectAt) {
        this.stream=stream;
        this.modifier=modifier;
        this.injectAt=injectAt;
    }

    @Override
    public DatagramPacket receive() throws IOException {
        return stream.receive();
    }

    @Override
    public void send(DatagramPacket packet) throws IOException, InvalidMessageException {
        System.out.println("Sending after receiving "+getNumberPacketsOfPackets()+" packets");
        if(getNumberPacketsOfPackets()==injectAt) {
            stream.send(modifier.modifyPacket(packet));
        }else {
            stream.send(packet);
        }

    }

    @Override
    public int getNumberPacketsOfPackets() {
        return stream.getNumberPacketsOfPackets();
    }

}
