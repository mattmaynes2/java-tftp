package core.net;

import core.net.NodeSocket;

import java.io.IOException;

public abstract class Transfer implements Runnable {

    public static final int BLOCK_SIZE = 512;

    private String filename;
    private NodeSocket socket;

    public Transfer (NodeSocket socket, String filename){
        this.socket = socket;
        this.filename = filename;
    }

    public String getFilename () {
        return this.filename;
    }

    public NodeSocket getSocket () {
        return this.socket;
    }

    public abstract void sendRequest(String filename) throws IOException;
}
