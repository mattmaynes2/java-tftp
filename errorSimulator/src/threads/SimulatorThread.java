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
import stream.SimulatorStream;

/**
 * Performs the communication between a client and a server once communication has been started
 *
 */
public  class SimulatorThread extends Thread {


    private DatagramPacket packetIn;
    private SocketAddress sendAddress;
    private SocketAddress clientAddress;
    private SocketAddress serverAddress;
    private SimulatorStream stream;
    
    /**
     * Creates a new socket and sets the timeout to 1000
     * @param packet  the datagram packet
     * @param simulation  the simulation to run from the ErrorSimulator
     * @param packetToModify  the packet to modify
     * @param modifier  the packet modifier
     * @throws SocketException  throws if a new socket cannot be created
     * @throws UnknownHostException  throws if a local host is unknown
     */
    //TODO change inputs so that there aren't as many and one won't potentially be null
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
        Logger.log(Level.INFO,"Received Packet From "+packetIn.getSocketAddress());
        try {
            Message msg=MessageFactory.createMessage(bytes);
            System.out.println(msg);
            sendPacket(msg, sendAddress);
            msg = receivePacket();
            serverAddress = packetIn.getSocketAddress(); //Server address must come from the first response to initial request
            while(!MessageFactory.isLastMessage(msg)){

                Logger.log(Level.INFO,"Message is "+msg);
                sendPacket(msg);
                msg=receivePacket();
            }
            sendPacket(msg); //forward last packet
            
            if(!OpCode.ERROR.equals(msg.getOpCode())) {
                //Receives the last packet if not an error
                msg=receivePacket();
                Logger.log(Level.INFO,"Message is "+msg);
                sendPacket(msg);
            }
        } catch (SocketException ex){
        	//socket closed
        }  catch (IOException | InvalidMessageException e) {
            e.printStackTrace();
        }
        Logger.log(Level.INFO, "Finished Simulation");
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
        Logger.log(Level.INFO,"Received Packet From "+packetIn.getSocketAddress());
        return MessageFactory.createMessage(bytes);
    }

    /**
     * Takes a message and uses it to create and send a packet
     * @param message The message to send
     * @throws IOException  throws if the stream cannot sent the packet
     * @throws InvalidMessageException  throws if the message format is invalid
     */
    protected void sendPacket(Message message) throws IOException, InvalidMessageException {
    	//Ensure packets are not send back to the sender in case of retransmitted/duplicated packets
    	if (packetIn.getSocketAddress().equals(serverAddress)){
    		sendAddress = clientAddress;
    	}else if(packetIn.getSocketAddress().equals(clientAddress)){
    		sendAddress = serverAddress;
    	}
    	sendPacket(message, sendAddress);
    }
    
    protected void sendPacket(Message message, SocketAddress address) throws IOException, InvalidMessageException{
		stream.send(new DatagramPacket(message.toBytes(), message.toBytes().length,sendAddress));
    }
}
