package core.cli;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Allows valid commands to defined for a worker, and is responsible for interpreting inputed commands
 *
 */
public class CommandInterpreter {

    /**
     * Stores the available commands that can be entered
     */
    private ArrayList<String> commands;

    /**
     * Constructs a new command interpreter
     */
    public CommandInterpreter () {
        this.commands = new ArrayList<String>();
    }

    /**
     * Adds a new valid command to the valid commands list
     * @param token  the command to add
     */
    public void addCommand(String token){
        this.commands.add(token);
    }

    /**
     * Takes a string of input and separates it into a command and its arguments
     * @param input the input to parse
     * @return a Command
     * @throws CommandInputException If the command is invalid
     */
    public Command parseCommand (String input) throws CommandInputException {
        StringTokenizer tokenizer = new StringTokenizer(input);
        String token;

        try {
        	token = tokenizer.nextToken().toLowerCase();
        } catch (NoSuchElementException ex) {
        	throw new CommandInputException("Please enter a command");
        }
        return this.interpretCommand(token, tokenizer);
    }

    /**
     * Given a parsed command, interpret the command and associate the
     * arguments of the command to the command token
     *
     * @param commandToken - Then invoked command (i.e. the command word)
     * @param tokenizer - The remaining un-interpreted token stream
     *
     * @return A compiled command
     */
    private Command interpretCommand (String commandToken, StringTokenizer tokenizer)
        throws CommandInputException {


        for(String token : this.commands){
            if(token.equals(commandToken)){
            	if(tokenizer.hasMoreTokens()) {
            		ArrayList<String> parameters = new ArrayList<String>();
            		while(tokenizer.hasMoreTokens()) {
            			parameters.add(tokenizer.nextToken());
            		}
            		return new Command(commandToken, parameters);
            	}

            	else {
            		return new Command(commandToken);
            	}
            }
        }

        throw new CommandInputException("Unknown command: " + commandToken + ". Type 'help' for instructions");
    }

}
