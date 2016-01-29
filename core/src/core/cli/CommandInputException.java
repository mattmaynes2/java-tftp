package core.cli;


/**
 * Custom Exception to catch if an error has occurred with command imput
 *
 */

@SuppressWarnings("serial")

public class CommandInputException extends Exception{

    public CommandInputException(String message){
        super(message);
    }

}
