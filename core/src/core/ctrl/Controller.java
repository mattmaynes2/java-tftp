package core.ctrl;

import core.req.Message;
import core.net.TransferListener;

import java.net.SocketAddress;

import core.cli.CLI;
import core.cli.Command;
import core.cli.CommandHandler;
import core.cli.CommandInterpreter;

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
     * Command to request usage information for this process
     */
    public static final String HELP_COMMAND = "help";

    /**
     * Address of endpoint to communicate with during transfer
     */
    protected SocketAddress address;

    /**
     * Command line interpreter for receiving user commands to control system
     */
    protected CommandInterpreter interpreter;

    /**
     * Command line interface to communicate with user
     */
    protected CLI cli;

    /**
     * Constructs a new controller with some default CLI commands
     */
    protected Controller () {
        this.interpreter = new CommandInterpreter();

        this.interpreter.addCommand(SHUTDOWN_COMMAND);
        this.interpreter.addCommand(HELP_COMMAND);
    }

    /**
     * Constructs a new controller to communicate to the given address
     *
     * @param address - Address of endpoint to communicate with
     */
    public Controller (SocketAddress address){
        this();
        this.address = address;
    }

    /**
     * Returns the address that this controller is communicating with
     *
     * @return Address of endpoint
     */
    public SocketAddress getAddress (){
        return this.address;
    }

    /**
     * Starts the controller and command line interface
     */
    public void start () {
        this.cli = new CLI(this.interpreter, System.in, System.out);
        this.cli.addCommandHandler(this);
        this.cli.start();
    }

    /**
     * Stops the controller and command line interface
     */
    public void stop () {
        this.cli.stop();
    }

    /**
     * Should print instructions about commands in the interface
     */
    public abstract void usage();

    /**
     * Invoked when a user types a command on the interface
     *
     * @param command - User's CLI command
     */
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

    /**
     * Invoked when a transfer is started
     */
    public abstract void handleStart ();

    /**
     * Invoked when a transfer receives a message from the endpoint
     *
     * @param msg - The message received
     */
    public abstract void handleMessage (Message msg);

    /**
     * Invoked when the transfer is complete
     */
    public abstract void handleComplete ();


}
