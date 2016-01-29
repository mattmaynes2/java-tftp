package core.log;

/**
 * Custom exception, thrown to indicate that logging has not been initializes
 */
@SuppressWarnings("serial")
public class UninitializedLoggerException extends RuntimeException{

    public UninitializedLoggerException(String message){
        super(message);
    }

}
