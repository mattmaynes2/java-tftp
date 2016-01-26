package core.cli;

public abstract class Command {
	
	protected CommandToken token;
	
	public abstract void execute(CommandHandler handler);

}
