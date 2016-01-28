package core.net;

import core.net.Transfer;
import core.net.NodeSocket;

import core.req.DataMessage;
import core.req.Message;
import core.req.ReadRequest;
import core.req.AckMessage;
import core.req.InvalidMessageException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

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

        this.notifyStart();

        try {

            out = new FileOutputStream(this.getFilename());
            msg = this.getData();

            while (msg.getData().length == Transfer.BLOCK_SIZE){
                this.notifyMessage(msg);
                this.forwardData(msg, out);
                msg = this.getData();
            }

            this.notifyMessage(msg);
            this.forwardData(msg, out);
            out.close();

            this.notifyComplete();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    private DataMessage getData () throws IOException, InvalidMessageException {
        DataMessage data;

        data = (DataMessage) this.getSocket().receive();
        this.getSocket().send(new AckMessage(data.getBlock()));

        return data;
    }

    private void forwardData(DataMessage msg, FileOutputStream out) throws IOException {
        out.write(msg.getData());
    }
}
