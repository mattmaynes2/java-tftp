package core.net;

import core.req.Message;

public interface TransferListener {

    public void handleStart ();

    public void handleSendMessage(Message msg);
    
    public void handleMessage (Message msg);

    public void handleComplete ();

}
