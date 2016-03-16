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
import java.util.logging.Logger;

import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageFactory;
import core.req.OpCode;
import stream.SimulatorStream;

/**
 * Performs the communication between a client and a server once communication has been started
 *
 */
public  class SimulatorThread extends Thread {

	private static final Logger LOGGER = Logger.getGlobal();
    private DatagramPacket packetIn;
    private SocketAddress sendAddress;
    private SocketAddress clientAddress;
    private SocketAddress serverAddress;
    private SimulatorStream stream;
    private SimulationEventListener eventListener;

    /**
     * Creates a new socket and sets the timeout to 1000
     * @param packet  the datagram packet
     * @param stream the simulation to run from the ErrorSimulator
     * @throws SocketException  throws if a new socket cannot be created
     * @throws UnknownHostException  throws if a local host is unknown
     */
    public SimulatorThread(DatagramPacket packet, SimulatorStream stream) throws SocketException, UnknownHostException {
        this.packetIn=packet;
        this.sendAddress= new InetSocketAddress(InetAddress.getLocalHost(),69);
        this.stream = stream;
        this.clientAddress = packet.getSocketAddress(); //save address of client from initial request
    }

    /**
     * Allows the thread to run.  Completes an entire transaction
     */
    @Override
    public void run() {
        byte[] bytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
        LOGGER.log(Level.INFO,"Received Packet From "+packetIn.getSocketAddress());
        if (eventListener != null){
        	eventListener.simulationStarted();
        }
        try {
            Message msg=MessageFactory.createMessage(bytes);
            System.out.println(msg);
            sendPacket(msg, sendAddress);
            msg = receivePacket();
            serverAddress = packetIn.getSocketAddress(); //Server address must come from the first response to initial request
            while(!MessageFactory.isLastMessage(msg)){

                LOGGER.log(Level.INFO,"Message is "+msg);
                sendPacket(msg);
                msg=receivePacket();
            }
            sendPacket(msg);
            if(!OpCode.ERROR.equals(msg.getOpCode())) {
                //Receives the last packet if not an error
                msg=receivePacket();
                LOGGER.log(Level.INFO,"Message is "+msg);
            }
            while (!sendPacket(msg) || (!msg.getOpCode().equals(OpCode.ACK) && !OpCode.ERROR.equals(msg.getOpCode()))){
            	msg = receivePacket();
            }

        } catch (SocketException ex){
        	//socket closed
        }  catch (IOException | InvalidMessageException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "Finished Simulation");
        if (eventListener != null){
        	eventListener.simulationComplete();
        }
    }

    public void subscribeSimulationEvents(SimulationEventListener listener){
    	eventListener = listener;
    }


    /**
     * Receives a packet and returns it as a Message
     * @return Message created from received packet
     * @throws IOException  throws if invalid input is entered
     * @throws InvalidMessageException  throws if the message format is invalid
     */
    protected Message receivePacket() throws IOException, InvalidMessageException {
        packetIn=stream.receive();
        byte[] bytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
        LOGGER.log(Level.INFO,"Received Packet From "+packetIn.getSocketAddress());
        return MessageFactory.createMessage(bytes);
    }

    /**
     * Takes a message and uses it to create and send a packet
     * @param message The message to send
     * @return If the packet was sent
     * @throws IOException  throws if the stream cannot sent the packet
     * @throws InvalidMessageException  throws if the message format is invalid
     */
    protected boolean sendPacket(Message message) throws IOException, InvalidMessageException {
    	//Ensure packets are not send back to the sender in case of retransmitted/duplicated packets
    	if (packetIn.getSocketAddress().equals(serverAddress)){
    		sendAddress = clientAddress;
    	}else if(packetIn.getSocketAddress().equals(clientAddress)){
    		sendAddress = serverAddress;
    	}
    	return sendPacket(message, sendAddress);
    }

    protected boolean sendPacket(Message message, SocketAddress address) throws IOException, InvalidMessageException{
		return stream.send(new DatagramPacket(message.toBytes(), message.toBytes().length,sendAddress));
    }
}
