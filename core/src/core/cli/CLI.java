package core.cli;

import core.util.Worker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This defines the functionality for a generic command line interface to be used by the client, errorSimulator,
 * and Server
 *
 */
public class CLI extends Worker {

    private InputStream in;
    private OutputStream out;
    private ArrayList<CommandHandler> handlers;
    private final String PROMPT = "tftp> ";
    private CommandInterpreter interpreter;
    private Scanner scanner;

    /**
     *
     * @param interpreter  the interpreter for the commands
     * @param in  the input stream to read from
     * @param out  the output stream to write to
     */
    public CLI(CommandInterpreter interpreter, InputStream in, OutputStream out){
        super();
        this.in =  in;
        this.out = out;
        this.handlers = new ArrayList<CommandHandler>();
        this.interpreter = interpreter;
    }

    /**
     * Sets up the scanner for the cli
     */
    public void setup (){
        this.scanner = new Scanner(this.in);
    }

    /**
     * closes the cil's scanner
     */
    public void teardown () {
        this.scanner.close();
    }

    /**
     * displays the prompt on console and waits for input
     */
    public void execute (){
        write(PROMPT);
        String line = scanner.nextLine();
        try {
            Command command = this.interpreter.parseCommand(line);
            notifyHandlers(command);
        } catch (CommandInputException e) {
            write(e.getMessage() + "\n");
        }
    }


    /**
     * Adds a new command handler
     * @param handler  the handler to add
     */
    public void addCommandHandler(CommandHandler handler){
        handlers.add(handler);
    }

    /**
     * Notifies all current handlers that a command has been received
     * @param command Command to send to handlers
     */
    public void notifyHandlers(Command command){
        for (CommandHandler handler : handlers){
            handler.handleCommand(command);
        }
    }

    /**
     * Writes the output to the output stream
     * @param output
     */
    private synchronized void write(String output){
        try {
        	if (this.isRunning()){
                out.write(output.getBytes());
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void message(String message){
    	write(message + "\n");
    }

    public void prompt(){
    	write(PROMPT);
    }
}
