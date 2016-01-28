import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import core.cli.CommandInterpreter;
import core.net.NodeSocket;
import core.net.TransferListener;
import core.req.Message;
import core.ctrl.TransferController;
import core.log.Logger;
import java.util.logging.Level;

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
        Logger.init(System.out,Level.FINEST);
        try {
            InetSocketAddress address =
                new InetSocketAddress(InetAddress.getLocalHost(), SERVER_PORT);

            client = new Client(address);
            client.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleStart() {
        this.cli.message("Transfer started");
    }

    @Override
    public void handleMessage(Message msg) {
        Logger.log("Client", Level.FINEST, "Received transfer message: " + msg.toString());
    }

    @Override
    public void handleComplete() {
        this.cli.message("Transfer complete");
    }
}
