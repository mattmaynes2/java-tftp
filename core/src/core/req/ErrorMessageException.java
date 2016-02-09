package core.req;

@SuppressWarnings("serial")
public class ErrorMessageException extends Exception {

    private ErrorMessage error;

    public ErrorMessageException (ErrorMessage error) {
        this.error = error;
    }

    public ErrorMessage getErrorMessage () {
        return this.error;
    }

}
