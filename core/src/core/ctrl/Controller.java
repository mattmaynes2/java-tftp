package core.ctrl;

import core.req.Message;
import core.req.ErrorMessage;

import core.net.TransferListener;

import core.cli.CLI;
import core.cli.Command;
import core.cli.CommandHandler;
import core.cli.CommandInterpreter;
import core.log.Logger;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;

/**
 * Controller
 *
 * Handles requests from a command line interface and spawns transfers
 */
public abstract class Controller implements CommandHandler{

    /**
     * Command line option to turn on quiet mode logging
     */
    private final static String QUIET_MODE_FLAG = "q";

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
     * Map of command line options specified by the user
     */
    protected Map<String, Boolean> commandLineOptions;

    /**
     * Constructs a new controller with some default CLI commands
     *
     * @param commandLineArgs - Arguments from the command line
     */
    protected Controller (String[] commandLineArgs) {
        this.interpreter = new CommandInterpreter();
        this.interpreter.addCommand(SHUTDOWN_COMMAND);
        this.interpreter.addCommand(HELP_COMMAND);
        this.commandLineOptions = new HashMap<String, Boolean>();
        setCommandLineOptions(commandLineArgs);
        applyCommandLineOptions();
    }

    /**
     * Constructs a new controller to communicate to the given address
     *
     * @param address - Address of endpoint to communicate with
     * @param commandLineArgs - Arguments from the command line
     */
    public Controller (SocketAddress address, String[] commandLineArgs){
        this(commandLineArgs);
        this.address = address;
    }

    /**
     * Sets the allowed command line options
     *
     * @param args - Set of options available
     */
    private void setCommandLineOptions(String[] args){
        for (int i=0; i < args.length; i++){
            if (args[i].startsWith("-") && args[i].length() > 1){
                setOption(args[i].substring(1));
            }
        }
    }

    /**
     * Adds an option to the available command line options
     *
     * @param option - The valid option that is to be added to the command set
     */
    private void setOption(String option){
        this.commandLineOptions.put(option, true);
    }

    /**
     * Applies the command line options to the given options
     */
    protected void applyCommandLineOptions(){
        if (this.commandLineOptions.getOrDefault(QUIET_MODE_FLAG, false)){
            Logger.init(Level.INFO);
        }else{
            Logger.init(Level.ALL);
        }
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
        this.usage();
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

}
