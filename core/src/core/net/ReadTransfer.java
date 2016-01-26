package core.net;

import core.net.NodeSocket;

import core.req.DataMessage;
import core.req.AckMessage;
import core.req.InvalidMessageException;

import java.io.IOException;
import java.io.OutputStream;

public class ReadTransfer {

    private NodeSocket socket;

    public ReadTransfer(NodeSocket socket){
        this.socket = socket;
    }

    public DataMessage getData () throws IOException, InvalidMessageException {
        DataMessage msg;

        msg = (DataMessage) this.socket.receive();
        this.socket.send(new AckMessage(msg.getBlock()));

        return msg;
    }

    public void forwardData(DataMessage msg, OutputStream out) throws IOException {
        out.write(msg.getData());
    }

}
