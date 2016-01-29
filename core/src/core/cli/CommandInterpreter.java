package core.cli;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Allows valid commands to defined for a worker, and is responsible for interpreting inputed commands
 *
 */
public class CommandInterpreter {

    private ArrayList<String> commands ;

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
     * @param input  the input to parse
     * @return  a Command
     * @throws CommandInputException
     */
    public Command parseCommand(String input) throws CommandInputException {
        StringTokenizer tokenizer = new StringTokenizer(input);
        String token;
        
        try{
        	token = tokenizer.nextToken().toLowerCase();
        }catch(NoSuchElementException ex){
        	throw new CommandInputException("Please enter a command");
        }
        return this.interpretCommand(token, tokenizer);
    }

    private Command interpretCommand(String commandToken, StringTokenizer tokenizer)
        throws CommandInputException {

        for(String token : this.commands){
            if(token.equals(commandToken)){
                return tokenizer.hasMoreTokens() ?
                    new Command(commandToken, tokenizer.nextToken()) :
                    new Command(commandToken);
            }
        }

        throw new CommandInputException("Unknown command: " + commandToken + ". Type 'help' for instructions");
    }

}
