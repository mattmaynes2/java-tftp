import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageFactory;
import core.util.ByteUtils;

public class SimulatorThread extends Thread {


    private DatagramSocket socket;
    private DatagramPacket packetIn;
    private SocketAddress sendAddress;

    public SimulatorThread(DatagramPacket packet) throws SocketException, UnknownHostException {
        this.packetIn=packet;
        this.sendAddress= new InetSocketAddress(InetAddress.getLocalHost(),69);
        socket= new DatagramSocket();
        socket.setSoTimeout(1000);
    }

    @Override
    public void run() {
        byte[] bytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
        System.out.println("Received Packet From "+packetIn.getSocketAddress());
        System.out.println("Bytes are: "+ByteUtils.bytesToHexString(bytes));
        try {
            Message msg=MessageFactory.createMessage(bytes);
            System.out.println(msg);
            sendPacket(msg);
            while(true) {
                msg=receivePacket();
                System.out.println("Message is "+msg);
                sendPacket(msg);
            }
        } catch (IOException | InvalidMessageException e) {
            e.printStackTrace();
        }
    }

    private Message receivePacket() throws IOException, InvalidMessageException {
        socket.receive(packetIn);
        byte[] bytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
        System.out.println("Received Packet From "+packetIn.getSocketAddress());
        System.out.print("Bytes are: "+ByteUtils.bytesToHexString(packetIn.getData()));
        return MessageFactory.createMessage(bytes);
    }

    private void sendPacket(Message message) throws IOException {
        socket.send(new DatagramPacket(message.toBytes(), message.toBytes().length,sendAddress));
        sendAddress=packetIn.getSocketAddress();
        System.out.println("Set next address to send "+sendAddress);
    }
}
