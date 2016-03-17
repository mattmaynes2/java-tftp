package core.net;

/**
 * A node socket handler responds to events generated
 * from a socket during normal execution
 */
public interface NodeSocketListener {

    /**
     * Should handle a timeout
     *
     * @param remaining - The number of timeouts remaining
     */
    public void handleTimeout (int remaining);

    /**
     * Should handle an unknown TID
     */
    public void handleUnknownTID ();

}
