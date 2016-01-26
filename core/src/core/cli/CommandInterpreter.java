package core.cli;

import core.cli.CommandToken;
import core.cli.Command;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class CommandInterpreter {

    private HashMap<CommandToken, Class<Command>> commands;

    public CommandInterpreter () {
        this.commands = new HashMap<CommandToken, Class<Command>>();
    }

    public void addCommand(Command command, Class<Command> constructor){
        this.commands.put(command.getToken(), constructor);
    }

    public Command parseCommand(String input) throws CommandInputException {
        StringTokenizer tokenizer = new StringTokenizer(input);

        return this.interpretCommand(tokenizer.nextToken().toLowerCase(), tokenizer);
    }

    private Command interpretCommand(String commandToken, StringTokenizer tokenizer)
        throws CommandInputException {

        try {
            for(CommandToken token : this.commands.keySet()){
                if(token.getToken().equals(commandToken)){
                    return token.getArgumentCount() == 0 ?
                        this.commands.get(token).getConstructor().newInstance() :
                        this.commands.get(token).getConstructor(String.class).newInstance(tokenizer.nextToken());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        throw new CommandInputException("Unknown command: " + commandToken + ". Type 'help' for instructions");
    }

}
