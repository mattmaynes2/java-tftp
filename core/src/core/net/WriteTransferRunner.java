package core.net;

import core.net.WriteTransfer;
import core.net.NodeSocket;

import java.io.InputStream;

public class WriteTransferRunner implements Runnable {

    private NodeSocket socket;
    private InputStream in;

    public WriteTransferRunner (NodeSocket socket, InputStream in){
        this.socket = socket;
        this.in = in;
    }

    public void run () {
        WriteTransfer transfer;

        try {
            transfer = new WriteTransfer(this.socket);

            while(transfer.sendData(this.in)){
                transfer.getAcknowledge();
            }

        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

}
