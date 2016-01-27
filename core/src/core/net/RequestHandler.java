package core.net;

import java.net.SocketAddress;

import core.req.Request;

public interface RequestHandler {

    public void handleRequest (Request req, SocketAddress requestAddress);

}
