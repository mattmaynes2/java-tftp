package core.net;

import core.net.Transfer;
import core.net.NodeSocket;

import core.req.DataMessage;
import core.req.Message;
import core.req.ReadRequest;
import core.req.AckMessage;
import core.req.InvalidMessageException;

import java.io.OutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class ReadTransfer extends Transfer {

    private NodeSocket socket;
    private OutputStream out;

    public ReadTransfer (NodeSocket socket, OutputStream out){
        this.socket = socket;
        this.out = out;
        this.logger = Logger.getLogger("readTransfer");
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
        DataMessage data;
        AckMessage ack;
        
        data = (DataMessage) this.socket.receive();
        this.logMessage(data);
        
        ack = new AckMessage(data.getBlock());
        this.logMessage(ack);
        this.socket.send(ack);

        return data;
    }

    private void forwardData(DataMessage msg, OutputStream out) throws IOException {
        out.write(msg.getData());
    }
}
