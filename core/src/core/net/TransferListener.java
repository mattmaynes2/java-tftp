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
     */
    public void handleSendMessage (Message msg);

    /**
     * Invoked when an error message has been received
     */
    public void handleErrorMessage (ErrorMessage err);

    /**
     * Invoked when a message has been received
     */
    public void handleMessage (Message msg);

    /**
     * Invoked when a transfer has been completed
     */
    public void handleComplete ();

}
