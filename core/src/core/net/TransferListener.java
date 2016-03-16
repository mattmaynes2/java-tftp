package core.net;

import core.req.Message;
import core.req.ErrorMessage;

/**
 * Transfer Listener
 *
 * Listens to the stages of a transfer
 */
public interface TransferListener {

    /**
     * Invoked when a transfer is started
     */
    public void handleStart ();

    /**
     * Invoked when a send message is send from a transfer
     *
     * @param msg - Message being sent
     */
    public void handleSendMessage (Message msg);

    /**
     * Invoked when an error message has been received
     *
     * @param err - Error message that was received
     */
    public void handleErrorMessage (ErrorMessage err);

    /**
     * Invoked when a message has been received
     *
     * @param msg - Message that was received
     */
    public void handleMessage (Message msg);

    /**
     * Invoked when a transfer has been completed
     */
    public void handleComplete ();

	/**
	 * Invoked when an exception occurred during transfer
	 * @param e the exception that was thrown
	 */
	public void handleException(Exception e);

	/**
	 * Invoked when there is a message to display to the user
	 * @param info message to display
	 */
	public void handleInfo(String info);

}
