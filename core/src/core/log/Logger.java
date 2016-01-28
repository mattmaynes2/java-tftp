package core.log;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class Logger {
	
	private static Level systemLogLevel = null;
	
	public static synchronized void init(OutputStream stream, Level logLevel){
		systemLogLevel = logLevel;
		java.util.logging.Logger.getGlobal().addHandler(new StreamHandler(stream, new SimpleFormatter()));
		java.util.logging.Logger.getGlobal().setLevel(logLevel);
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