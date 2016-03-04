package core.log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * static wrapper for java utils Logger
 * logs messages to System.out
 * Allows a global log level to be set for all log messages
 */
public class ConsoleLogger {
	
	/**
	 * Initializes global logging with the specified log level
	 * @param logLevel The minimum severity of messages to log
	 */
	public static synchronized void init(Level logLevel){
		// disable passing the logs up to the default parent logger
		java.util.logging.Logger.getGlobal().setUseParentHandlers(false);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new Formatter() {
			
			private String calcDate(long millisecs) {
			    SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
			    Date resultdate = new Date(millisecs);
			    return date_format.format(resultdate);
			  }

			@Override
			public String format(LogRecord record) {
				StringBuffer buf = new StringBuffer();
				buf.append("\n["+Thread.currentThread().getName()+" ");
				buf.append(calcDate(record.getMillis())+"]");
				buf.append(" "+record.getSourceClassName()+" "+record.getLevel()+": "+record.getMessage()+"\n");
				return buf.toString();
			}
		});
		handler.setLevel(logLevel);
		LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(logLevel);
		Logger.getGlobal().addHandler(handler);
	}
	
}