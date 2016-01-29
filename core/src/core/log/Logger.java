package core.log;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

public class Logger {
	
	private static Level systemLogLevel = null;
	
	
	public static synchronized void init(Level logLevel){
		systemLogLevel = logLevel;
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(logLevel);
		java.util.logging.Logger.getGlobal().addHandler(handler);
		java.util.logging.Logger.getGlobal().setLevel(logLevel);
	}
	
	public static void log(Level logLevel,String message) throws UninitializedLoggerException{
		if (systemLogLevel == null){
			throw new UninitializedLoggerException("Logger has not been initialized");
		}else{
			java.util.logging.Logger.getGlobal().log(logLevel, message);
		}
	}
}