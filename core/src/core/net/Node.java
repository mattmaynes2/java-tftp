package core.net;

import core.net.NodeSocket;

import core.cli.CLI;
import core.cli.CommandHandler;
import core.cli.CommandInterpreter;
import core.cli.Command;

import java.net.SocketException;

public abstract class Node implements CommandHandler {

    NodeSocket socket;
    CommandInterpreter interpreter;

    private CLI cli;

    public Node () throws SocketException {
        this.socket = new NodeSocket();
    }

    public Node (int port) throws SocketException {
        this.socket = new NodeSocket(port);
    }

    public void start () {
        this.cli = new CLI(this.interpreter, System.in, System.out);
        this.cli.addCommandHandler(this);
        (new Thread(this.cli)).start();
    }

    public void stop () {
        this.socket.close();
        this.cli.stop();
    }

    public abstract void usage();

    public void handleCommand (Command command){
        switch(NodeCommand.createCommand(command.getToken())){
            case SHUTDOWN:
                this.stop();
                break;
            case HELP:
                this.usage();
                break;
        }
    }

}
