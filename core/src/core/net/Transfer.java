package core.net;

import java.io.IOException;

public abstract class Transfer implements Runnable {

    public static final int BLOCK_SIZE = 512;

    public abstract void sendRequest(String filename) throws IOException;
}
