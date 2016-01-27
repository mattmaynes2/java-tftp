package core.net;

import core.net.Transfer;
import core.net.NodeSocket;

import core.req.DataMessage;
import core.req.AckMessage;
import core.req.InvalidMessageException;

import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;

public class WriteTransfer extends Transfer {

    private NodeSocket socket;
    private short currentBlock;

    public WriteTransfer (NodeSocket socket){
        this.socket = socket;
        this.currentBlock = 1;
    }

    public AckMessage getAcknowledge () throws IOException, InvalidMessageException {
        return (AckMessage) this.socket.receive();
    }

    public boolean sendData (InputStream in) throws IOException {
        DataMessage msg = this.createMessage(in);
        this.socket.send(msg);
        return msg.getData().length > 0;
    }

    private DataMessage createMessage(InputStream in) throws IOException {
        int read;
        byte[] data = new byte[Transfer.BLOCK_SIZE];

        read = in.read(data, Transfer.BLOCK_SIZE * (this.currentBlock - 1), Transfer.BLOCK_SIZE);
        return new DataMessage(this.currentBlock, Arrays.copyOfRange(data, 0, read));
    }

}
