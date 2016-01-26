package core.cli;

import core.cli.Command;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class CommandInterpreter {

    private ArrayList<String> commands ;

    public CommandInterpreter () {
        this.commands = new ArrayList<String>();
    }

    public void addCommand(String token){
        this.commands.add(token);
    }

    public Command parseCommand(String input) throws CommandInputException {
        StringTokenizer tokenizer = new StringTokenizer(input);

        return this.interpretCommand(tokenizer.nextToken().toLowerCase(), tokenizer);
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
