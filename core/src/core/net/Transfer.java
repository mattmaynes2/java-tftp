package core.net;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.req.Message;

public abstract class Transfer implements Runnable {

    public static final int BLOCK_SIZE = 512;
    
    protected Logger logger;
    
    public abstract void sendRequest(String filename) throws IOException;
    
	public void logMessage(Message msg) {
		this.logger.log(Level.INFO, msg.toString());
	}
}
