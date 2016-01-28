package core.net;

import core.net.NodeSocket;

import core.req.WriteRequest;
import core.req.DataMessage;
import core.req.AckMessage;
import core.req.InvalidMessageException;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.logging.Logger;

public class WriteTransfer extends Transfer {

    private short currentBlock;

    public WriteTransfer (NodeSocket socket, String filename){
        super(socket, filename);
        this.currentBlock = 0;
    }

    public void sendRequest (String filename) throws IOException {
        this.getSocket().send(new WriteRequest(filename));
    }

    public void run () {
        FileInputStream in;

        this.notifyStart();

        try {
            in = new FileInputStream(this.getFilename());

            while (this.sendData(in)){
                this.notifyMessage(this.getAcknowledge());
            }

            this.notifyComplete();

            in.close();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public AckMessage getAcknowledge () throws IOException, InvalidMessageException {
        return (AckMessage) this.getSocket().receive();
    }

    private boolean sendData (FileInputStream in) throws IOException {
        DataMessage msg = this.createMessage(in);
        this.getSocket().send(msg);
        return msg.getData().length > 0;
    }

    private DataMessage createMessage(FileInputStream in) throws IOException {
        DataMessage message;
        int read;
        byte[] data = new byte[Transfer.BLOCK_SIZE];

        this.currentBlock++;
        read = in.read(data);

        if (read >= 0) {
            message = new DataMessage(this.currentBlock, Arrays.copyOfRange(data, 0, read));
        }
        else {
            message =  new DataMessage(this.currentBlock, new byte[0]);
        }

        return message;
    }

}
