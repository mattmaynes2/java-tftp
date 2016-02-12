package threads;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;

import core.log.Logger;
import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageFactory;
import core.req.OpCode;
import sim.PacketModifier;
import sim.SimulationTypes;
import stream.SimulatorStream;
import stream.SimulatorStreamFactory;

/**
 * Performs the communication between a client and a server once communication has been started
 *
 */
public  class SimulatorThread extends Thread {


    private DatagramPacket packetIn;
    private SocketAddress sendAddress;
    private SimulatorStream stream;
    /**
     * Creates a new socket and sets the timeout to 1000
     * @param packet  the datagram packet
     * @throws SocketException
     * @throws UnknownHostException
     */
    //TODO change inputs so that there aren't as many and one won't potentially be null
    public SimulatorThread(DatagramPacket packet, SimulationTypes simulation,int packetToModify, PacketModifier modifier) throws SocketException, UnknownHostException {
        this.packetIn=packet;
        this.sendAddress= new InetSocketAddress(InetAddress.getLocalHost(),69);
        this.stream=SimulatorStreamFactory.createSimulationStream(simulation, modifier, packetToModify);
    }

    /**
     * Allows the thread to run.  Completes an entire transaction
     */
    @Override
    public void run() {
        byte[] bytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
        Logger.log(Level.INFO,"Received Packet From "+packetIn.getSocketAddress());
        try {
            Message msg=MessageFactory.createMessage(bytes);
            System.out.println(msg);
            sendPacket(msg);
            while(!MessageFactory.isLastMessage(msg)) {

                msg=receivePacket();
                Logger.log(Level.INFO,"Message is "+msg);
                sendPacket(msg);
            }
            if(!OpCode.ERROR.equals(msg.getOpCode())) {
                //Receives the last packet if not an error
                msg=receivePacket();
                Logger.log(Level.INFO,"Message is "+msg);
                sendPacket(msg);
            }
        } catch (IOException | InvalidMessageException e) {
            e.printStackTrace();
        }
        Logger.log(Level.INFO, "Finished Simulation");
    }



    /**
     * Receives a packet and returns it as a Message
     * @return Message created from received packet
     * @throws IOException
     * @throws InvalidMessageException
     */
    protected Message receivePacket() throws IOException, InvalidMessageException {
        packetIn=stream.receive();
        byte[] bytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
        Logger.log(Level.INFO,"Received Packet From "+packetIn.getSocketAddress());
        return MessageFactory.createMessage(bytes);
    }

    /**
     * Takes a message and uses it to create and send a packet
     * @param message
     * @throws IOException
     * @throws InvalidMessageException
     */
    protected void sendPacket(Message message) throws IOException, InvalidMessageException {
        stream.send(new DatagramPacket(message.toBytes(), message.toBytes().length,sendAddress));
        sendAddress=packetIn.getSocketAddress();
    }
}
