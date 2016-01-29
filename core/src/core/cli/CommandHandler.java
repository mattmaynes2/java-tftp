package core.cli;

/**
 * Defines the interfaces for a command handler
 *
 */
public interface CommandHandler {

	/**
	 * Each command handler will have its own way of handling a command
	 * @param command the command to handle
	 */
	public void handleCommand(Command command);
}
