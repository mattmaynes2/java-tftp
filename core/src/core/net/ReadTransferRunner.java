package core.net;

import core.net.Transfer;
import core.net.ReadTransfer;
import core.net.NodeSocket;

import core.req.DataMessage;
import core.req.ReadRequest;

import java.io.OutputStream;
import java.io.IOException;

public class ReadTransferRunner implements Runnable {

    private NodeSocket socket;
    private OutputStream out;

    public ReadTransferRunner (NodeSocket socket, OutputStream out){
        this.socket = socket;
        this.out = out;
    }

    public void sendRequest (String filename) throws IOException {
        this.socket.send(new ReadRequest(filename));
    }

    public void run () {
        DataMessage msg;
        ReadTransfer transfer;

        try {
            transfer = new ReadTransfer(this.socket);

            msg = transfer.getData();

            while (msg.getData().length == Transfer.BLOCK_SIZE){
                transfer.forwardData(msg, this.out);
                msg = transfer.getData();
            }

            transfer.forwardData(msg, this.out);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

}
