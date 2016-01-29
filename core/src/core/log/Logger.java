package core.log;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * static wrapper for java utils Logger
 * logs messages to System.out
 * Allows a global log level to be set for all log messages
 */
public class Logger {
	
	private static Level systemLogLevel = null;
	
	/**
	 * Initializes global logging with the specified log level
	 * @param logLevel The minimum severity of messages to log
	 */
	public static synchronized void init(Level logLevel){
		// disable passing the logs up to the default parent logger
		java.util.logging.Logger.getGlobal().setUseParentHandlers(false);
		systemLogLevel = logLevel;
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(logLevel);
		java.util.logging.Logger.getGlobal().addHandler(handler);
		java.util.logging.Logger.getGlobal().setLevel(logLevel);
	}
	
	/**
	 * logs a message to the console
	 * @param logLevel The severity level of the message
	 * @param message The message to log
	 * @throws UninitializedLoggerException If logging has not been initialized
	 */
	public static void log(Level logLevel,String message) throws UninitializedLoggerException{
		if (systemLogLevel == null){
			throw new UninitializedLoggerException("Logger has not been initialized");
		}else{
			java.util.logging.Logger.getGlobal().log(logLevel, message + "\n");
		}
	}
}