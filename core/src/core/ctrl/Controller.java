package core.ctrl;

import core.req.Message;

import core.net.NodeSocket;
import core.net.TransferListener;

import core.cli.CLI;
import core.cli.CommandHandler;
import core.cli.CommandInterpreter;
import core.cli.Command;

import java.net.SocketException;
import java.net.SocketAddress;

/**
 * Controller
 *
 * Handles requests from a command line interface and spawns transfers
 */
public abstract class Controller implements CommandHandler, TransferListener {

    /**
     * Command to shutdown this controller
     */
    public static final String SHUTDOWN_COMMAND = "shutdown";

    /**
     * Command to request usage information for this proces
     */
    public static final String HELP_COMMAND = "help";

    /**
     * Address
     */
    protected SocketAddress address;
    protected CommandInterpreter interpreter;

    protected CLI cli;

    protected Controller () {
        this.interpreter = new CommandInterpreter();

        this.interpreter.addCommand(SHUTDOWN_COMMAND);
        this.interpreter.addCommand(HELP_COMMAND);

    }

    public Controller (SocketAddress address){
        this();
        this.address = address;
    }

    public SocketAddress getAddress (){
        return this.address;
    }

    public void start () {
        this.cli = new CLI(this.interpreter, System.in, System.out);
        this.cli.addCommandHandler(this);
        this.cli.start();
    }

    public void stop () {
        this.cli.stop();
    }

    public abstract void usage();

    public void handleCommand (Command command){
        switch (command.getToken()){
            case SHUTDOWN_COMMAND:
                this.stop();
                break;
            case HELP_COMMAND:
                this.usage();
                break;
            default:
                break;
        }
    }

    public abstract void handleStart ();

    public abstract void handleMessage (Message msg);

    public abstract void handleComplete ();


}
