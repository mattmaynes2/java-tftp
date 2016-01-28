package core.log;
import java.util.logging.Level;

public class Logger {
	
	private static Level systemLogLevel = null;
	
	public static synchronized void init(Level logLevel){
		systemLogLevel = logLevel;
	}
	
	public static void log(String className, Level logLevel,String message) throws UnitializedLoggerException{
		if (systemLogLevel == null){
			throw new UnitializedLoggerException("Logger has not been initialized");
		}else{
			java.util.logging.Logger logger = java.util.logging.Logger.getLogger(className);
			logger.setLevel(systemLogLevel);
			logger.log(logLevel, message);
		}
	}
}