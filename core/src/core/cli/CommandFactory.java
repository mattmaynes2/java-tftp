package core.cli;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class CommandFactory {
	
	static final String READ_COMMAND_INPUT = "read";
	static final String WRITE_COMMAND_INPUT = "write";
	static final String SHUTDOWN_COMMAND_INPUT = "shutdown";
	
	static Command createCommand(String input) throws CommandInputException{
		StringTokenizer tokenizer = new StringTokenizer(input);
		Command returnCommand;
		
		try {
			String commandToken = tokenizer.nextToken().toLowerCase();
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
				default:
					throw new CommandInputException("Unknown command in input:\n " + input);
			}
		}catch(NoSuchElementException ex){
			throw new CommandInputException("Input does not contain a valid command:\n " + input);
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
	
}
