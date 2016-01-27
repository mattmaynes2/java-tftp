package core.net;

import core.net.Transfer;
import core.net.NodeSocket;

import core.req.DataMessage;
import core.req.ReadRequest;
import core.req.AckMessage;
import core.req.InvalidMessageException;

import java.io.OutputStream;
import java.io.IOException;

public class ReadTransfer extends Transfer {

    private NodeSocket socket;
    private OutputStream out;

    public ReadTransfer (NodeSocket socket, OutputStream out){
        this.socket = socket;
        this.out = out;
    }

    public void sendRequest (String filename) throws IOException {
        this.socket.send(new ReadRequest(filename));
    }

    public void run () {
        DataMessage msg;

        try {
            msg = this.getData();

            while (msg.getData().length == Transfer.BLOCK_SIZE){
                this.forwardData(msg, this.out);
                msg = this.getData();
            }

            this.forwardData(msg, this.out);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    private DataMessage getData () throws IOException, InvalidMessageException {
        DataMessage msg;

        msg = (DataMessage) this.socket.receive();
        this.socket.send(new AckMessage(msg.getBlock()));

        return msg;
    }

    private void forwardData(DataMessage msg, OutputStream out) throws IOException {
        out.write(msg.getData());
    }

}
