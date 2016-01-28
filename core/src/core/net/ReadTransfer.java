package core.net;

import core.net.Transfer;
import core.net.NodeSocket;

import core.req.DataMessage;
import core.req.ReadRequest;
import core.req.AckMessage;
import core.req.InvalidMessageException;

import java.io.FileOutputStream;
import java.io.IOException;

public class ReadTransfer extends Transfer {

    public ReadTransfer (NodeSocket socket, String filename){
        super(socket, filename);
    }

    public void sendRequest (String filename) throws IOException {
        this.getSocket().send(new ReadRequest(filename));
    }

    public void run () {
        FileOutputStream out;
        DataMessage msg;

        try {
            out = new FileOutputStream(this.getFilename());
            msg = this.getData();

            while (msg.getData().length == Transfer.BLOCK_SIZE){
                this.forwardData(msg, out);
                msg = this.getData();
            }

            this.forwardData(msg, out);
            out.close();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    private DataMessage getData () throws IOException, InvalidMessageException {
        DataMessage msg;

        msg = (DataMessage) this.getSocket().receive();
        this.getSocket().send(new AckMessage(msg.getBlock()));

        return msg;
    }

    private void forwardData(DataMessage msg, FileOutputStream out) throws IOException {
        out.write(msg.getData());
    }

}
