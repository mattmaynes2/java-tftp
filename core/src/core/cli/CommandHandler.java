package core.cli;

public interface CommandHandler {

	public void handleReadCommand(ReadCommand command);
	public void handleWriteCommand(WriteCommand command);
	public void handleShutdownCommand(ShutdownCommand command);
	public void handleHelpCommand(HelpCommand command);
}
