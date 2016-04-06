package core.ctrl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.cli.CLI;
import core.cli.Command;
import core.cli.CommandHandler;
import core.cli.CommandInterpreter;
import core.log.ConsoleLogger;

/**
 * Controller
 *
 * Handles requests from a command line interface and spawns transfers
 */
public abstract class Controller implements CommandHandler {

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
     * Command to set the file write destination
     */
    public static final String CHANGE_DIRECTORY_COMMAND = "cd";

    /**
     * Address of endpoint to communicate with during transfer
     */
    protected InetSocketAddress address;

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
     *
     */
    protected static final Logger LOGGER = Logger.getGlobal();

    /**
     *
     */
    private String directoryPrefix = "";

    /**
     * Constructs a new controller with some default CLI commands
     *
     * @param commandLineArgs - Arguments from the command line
     */
    protected Controller (String[] commandLineArgs) {
        this.interpreter = new CommandInterpreter();
        this.interpreter.addCommand(SHUTDOWN_COMMAND);
        this.interpreter.addCommand(HELP_COMMAND);
        this.interpreter.addCommand(CHANGE_DIRECTORY_COMMAND);
        this.commandLineOptions = new HashMap<String, Boolean>();
        this.setCommandLineOptions(commandLineArgs);
        this.applyCommandLineOptions();
    }

    /**
     * Constructs a new controller to communicate to the given address
     *
     * @param address - Address of endpoint to communicate with
     * @param commandLineArgs - Arguments from the command line
     */
    public Controller (InetSocketAddress address, String[] commandLineArgs){
        this(commandLineArgs);
        this.address = address;
    }

    /**
     * Sets the address this endpoint is communicating with
     * @param address The address to communicate with
     */
    public void setAddress(InetSocketAddress address){
        if (address == null)
        {
            throw new IllegalArgumentException("Address cannot be null");
        }
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
            ConsoleLogger.init(Level.INFO);
        }else{
            ConsoleLogger.init(Level.ALL);
        }
    }

    /**
     * Returns the address that this controller is communicating with
     *
     * @return Address of endpoint
     */
    public InetSocketAddress getAddress (){
        return this.address;
    }

    /**
     * Returns the directory prefix that this controller is
     * reading and writing from
     *
     * @return Prefix of directory
     */
    public String getPrefix() {
        return this.directoryPrefix;
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
     * Changes the current working directory
     *
     * @param dir - the new working directory
     */
    public void changeWorkingDirectory(String dir) {
        if (dir.endsWith("/")){
            this.directoryPrefix = dir;
        }else{
            this.directoryPrefix = dir + "/";
        }
    }


    /**
     * Appends the current directory prefix to the given path
     *
     * @param filepath - Path to prepend prefix to
     *
     * @return Complete path
     */
    public String appendPrefix(String filepath) {
        if(filepath.startsWith("/")|| filepath.startsWith(":/", 1) ||filepath.startsWith(":\\",1)) {
            return filepath;
        }
        return directoryPrefix.concat(filepath);
    }

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
            case CHANGE_DIRECTORY_COMMAND:
                try{
                    changeWorkingDirectory(concatPath(command.getArguments()));
                    this.cli.message("Relative directory is now: " + this.getPrefix());
                }catch (IndexOutOfBoundsException e) {
                    this.cli.message("Incorrect number of parameters for cd.  Format is cd filepath");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Takes all the input parameters and makes them one file path
     * @param arguments - the arguments to concatenate
     * @return the file path
     */
    protected String concatPath(ArrayList<String> arguments) {
        StringBuffer buf= new StringBuffer();
        for(String s:arguments){
            buf.append(s+" ");
        }
        buf.deleteCharAt(buf.length()-1);
        return buf.toString();

    }

}
