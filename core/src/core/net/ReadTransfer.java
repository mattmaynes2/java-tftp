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
        this.logger = Logger.getLogger("readTransfer");
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
        DataMessage data;
        AckMessage ack;
        
        data = (DataMessage) this.getSocket().receive();
        this.logMessage(data);
        
        ack = new AckMessage(data.getBlock());
        this.logMessage(ack);
        this.getSocket().send(ack);

        return data;
    }

    private void forwardData(DataMessage msg, FileOutputStream out) throws IOException {
        out.write(msg.getData());
    }
}
