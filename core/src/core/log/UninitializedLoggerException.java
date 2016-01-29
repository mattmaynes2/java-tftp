package core.log;

@SuppressWarnings("serial")
public class UninitializedLoggerException extends RuntimeException{

    public UninitializedLoggerException(String message){
        super(message);
    }

}
