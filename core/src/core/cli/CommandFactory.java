package core.cli;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class CommandFactory {
	
	static final String READ_COMMAND_INPUT = "read";
	static final String WRITE_COMMAND_INPUT = "write";
	static final String SHUTDOWN_COMMAND_INPUT = "exit";
	static final String HELP_COMMAND_INPUT = "help";

	
	static Command createCommand(String input) throws CommandInputException{
		StringTokenizer tokenizer = new StringTokenizer(input);
		Command returnCommand;
		
		try {
			String commandToken = tokenizer.nextToken().toLowerCase();
			returnCommand = createCommand(commandToken, tokenizer);
		}catch(NoSuchElementException ex){
			throw new CommandInputException("Enter a command");
		}
		return returnCommand;	
	}
	
	private static Command createCommand(String commandToken, StringTokenizer tokenizer) throws CommandInputException{
		Command returnCommand;

		try {
			switch(commandToken){
				case READ_COMMAND_INPUT:
					returnCommand = createReadCommand(tokenizer.nextToken().toLowerCase());
					break;
				case WRITE_COMMAND_INPUT:
					returnCommand = createWriteCommand(tokenizer.nextToken().toLowerCase());
					break;
				case SHUTDOWN_COMMAND_INPUT:
					returnCommand = createShutdownCommand();
					break;
				case HELP_COMMAND_INPUT:
					returnCommand = createHelpCommand();
					break;
				default:
					throw new CommandInputException("Unknown command: " + commandToken);
			}
		}catch(NoSuchElementException ex){
			throw new CommandInputException("Missing argument to command: " + commandToken);
		}
		return returnCommand;
	}
	
	private static Command createReadCommand(String fileName){
		return new ReadCommand(fileName);
	}
	
	private static Command createWriteCommand(String fileName){
		return new WriteCommand(fileName);
	}
	
	private static Command createShutdownCommand(){
		return new ShutdownCommand();
	}
	
	private static Command createHelpCommand(){
		return new HelpCommand();
	}
	
}
