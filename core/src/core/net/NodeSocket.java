
package core.net;

import core.req.Message;
import core.req.MessageFactory;
import core.req.InvalidMessageException;

import java.util.Arrays;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class NodeSocket {

    private SocketAddress address;
    private DatagramSocket socket;

    public NodeSocket () throws SocketException {
        this.socket = new DatagramSocket();
    }

    public NodeSocket (int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    public void send (Message message) throws IOException {
        this.send(message, this.address);
    }

    public void send (Message message, SocketAddress address) throws IOException {
        byte[] data = message.toBytes();
        this.socket.send(new DatagramPacket(data, data.length, address));
    }

    public Message receive() throws IOException, InvalidMessageException {
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

        this.socket.receive(packet);
        this.address = packet.getSocketAddress();

        return MessageFactory.createMessage(
            Arrays.copyOfRange(packet.getData(), 0, packet.getLength())
            );
    }

}
