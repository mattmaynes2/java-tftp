package core.net;

import core.net.NodeSocket;

import core.cli.CLI;
import core.cli.CommandHandler;
import core.cli.ReadCommand;
import core.cli.WriteCommand;
import core.cli.ShutdownCommand;
import core.cli.HelpCommand;

import java.net.SocketException;

public abstract class Node implements CommandHandler {

    NodeSocket socket;
    CLI cli;

    public Node () throws SocketException {
        this.socket = new NodeSocket();
    }

    public Node (int port) throws SocketException {
        this.socket = new NodeSocket(port);
    }

    public void stop () {
        this.socket.close();
        this.cli.stop();
    }

    public void handleShutdownCommand (ShutdownCommand command){
        this.stop();
    }

}
