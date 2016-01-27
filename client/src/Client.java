import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import core.cli.CommandInterpreter;
import core.net.NodeSocket;
import core.run.TransferController;

public class Client extends TransferController {

    private static final int SERVER_PORT = 69;

    public Client (SocketAddress address){
        super(address);
    }

    @Override
    public void usage() {

    }

    public static void main(String[] args){
        Client client;

        try {
            InetSocketAddress address =
                new InetSocketAddress(InetAddress.getLocalHost(), SERVER_PORT);

            client = new Client(address);
            client.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
