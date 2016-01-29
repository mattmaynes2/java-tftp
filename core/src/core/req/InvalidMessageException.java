package core.req;


/**
 * A custom exception that is thrown when an invalid message is received
 *
 */

@SuppressWarnings("serial")
public class InvalidMessageException extends Exception {

    public InvalidMessageException(String message) {
        super(message);
    }
}
