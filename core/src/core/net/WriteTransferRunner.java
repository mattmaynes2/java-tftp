package core.net;

import core.net.WriteTransfer;
import core.net.NodeSocket;

import core.req.WriteRequest;

import java.io.InputStream;
import java.io.IOException;

public class WriteTransferRunner implements Runnable {

    private NodeSocket socket;
    private InputStream in;

    public WriteTransferRunner (NodeSocket socket, InputStream in){
        this.socket = socket;
        this.in = in;
    }

    public void sendRequest (String filename) throws IOException {
        this.socket.send(new WriteRequest(filename));
    }

    public void run () {
        WriteTransfer transfer;

        try {
            transfer = new WriteTransfer(this.socket);

            while (transfer.sendData(this.in)){
                transfer.getAcknowledge();
            }

        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

}
