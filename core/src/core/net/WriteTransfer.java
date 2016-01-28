package core.net;

import core.net.NodeSocket;

import core.req.WriteRequest;
import core.req.DataMessage;
import core.req.AckMessage;
import core.req.InvalidMessageException;

import java.io.InputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.logging.Logger;

public class WriteTransfer extends Transfer {

    private NodeSocket socket;
    private InputStream in;
    private short currentBlock;

    public WriteTransfer (NodeSocket socket, InputStream in){
        this.socket = socket;
        this.in = in;
        this.currentBlock = 1;
        this.logger = Logger.getLogger("writeTransfer");
    }

    public void sendRequest (String filename) throws IOException {
        this.socket.send(new WriteRequest(filename));
    }

    public void run () {
        try {
        	while (this.sendData(this.in)){
                this.getAcknowledge();
            }

        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    private AckMessage getAcknowledge () throws IOException, InvalidMessageException {
        AckMessage ack = (AckMessage) this.socket.receive();
        this.logMessage(ack);
        return ack;
    }

    private boolean sendData (InputStream in) throws IOException {
        DataMessage msg = this.createMessage(in);
        this.socket.send(msg);
        this.logMessage(msg);
        return msg.getData().length > 0;
    }

    private DataMessage createMessage(InputStream in) throws IOException {
        int read;
        byte[] data = new byte[Transfer.BLOCK_SIZE];

        read = in.read(data, Transfer.BLOCK_SIZE * (this.currentBlock - 1), Transfer.BLOCK_SIZE);
        
        DataMessage message;
        
        if (read >= 0){
        	message = new DataMessage(this.currentBlock, Arrays.copyOfRange(data, 0, read));
        }else{
        	message =  new DataMessage(this.currentBlock, new byte[0]); 
        }
        return message;
    }

}
