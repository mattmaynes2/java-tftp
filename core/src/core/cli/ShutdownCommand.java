package core.cli;

public class ShutdownCommand extends Command {
	
	public void execute(CommandHandler handler){
		handler.handleShutdownCommand(this);
	}
}
