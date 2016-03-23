package stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.req.InvalidMessageException;

/**
 * DelayedPacketStream
 * Will delay a specific packet send on an underlying stream
 */
public class DelayedPacketStream extends SimulatorStream {

    private static final Logger LOGGER = Logger.getGlobal();
    private PacketStream stream;
    private int delayedPacketNumber;
    private int delayTime;
    private boolean alreadyDelayed = false;

    /**
     *
     * @param stream A PacketStream to delay packets for
     * @param delayedPacketNumber Index of the delayed packet
     * @param timeoutMilliseconds Number of milliseconds to wait before timing out
     */
    public DelayedPacketStream(PacketStream stream, int delayedPacketNumber, int timeoutMilliseconds){
        this.stream = stream;
        this.delayedPacketNumber = delayedPacketNumber;
        this.delayTime = timeoutMilliseconds;
    }

    @Override
    public DatagramPacket receive() throws IOException {
        return this.stream.receive();
    }

    @Override
    public boolean send(final DatagramPacket packet) throws IOException, InvalidMessageException {
        if ((stream.getNumberPacketsOfPackets() == delayedPacketNumber) && !alreadyDelayed){
            Thread t = new Thread(new Runnable(){
                public void run(){
                    try {
                        LOGGER.log(Level.INFO, "Delaying packet by " + delayTime + "ms : " + Arrays.toString(packet.getData()));
                        Thread.sleep(delayTime);
                        stream.send(packet);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            alreadyDelayed = true;
            t.start();
        }else{
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
