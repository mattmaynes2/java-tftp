package core.net;

import core.net.NodeSocket;

import core.req.DataMessage;
import core.req.AckMessage;
import core.req.InvalidMessageException;

import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;

public class WriteTransfer {

    private NodeSocket socket;
    private short currentBlock;
    private static final int DATA_SIZE = 512;

    public WriteTransfer (NodeSocket socket){
        this.socket = socket;
        this.currentBlock = 0;
    }

    public void getAcknowledge () throws IOException, InvalidMessageException {
        AckMessage ack = (AckMessage) this.socket.receive();
    }

    public boolean sendData (InputStream in) throws IOException {
        DataMessage msg = this.createMessage(in);
        this.socket.send(msg);
        return msg.getData().length > 0;
    }

    private DataMessage createMessage(InputStream in) throws IOException {
        int read;
        byte[] data = new byte[DATA_SIZE];

        read = in.read(data, DATA_SIZE * this.currentBlock, DATA_SIZE);

        return new DataMessage(this.currentBlock, Arrays.copyOfRange(data, 0, read));
    }

}
