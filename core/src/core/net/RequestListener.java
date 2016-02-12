package core.net;

import java.net.SocketAddress;

import core.req.ErrorMessage;
import core.req.Request;

/**
 * Request Listener
 *
 * Notifies a listener when a request is received
 */
public interface RequestListener {

    /**
     * Invoked when a request is received on a listener
     *
     * @param req - Request received
     * @param requestAddress - Address of request sender
     */
    public void handleRequest (Request req, SocketAddress requestAddress);

    /**
     * Invoked when an error occurs on a request
     *
     * @param err - Error Message
     */
    public void handleError (ErrorMessage err);

}
