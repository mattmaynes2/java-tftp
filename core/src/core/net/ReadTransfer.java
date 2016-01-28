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

/**
 * ReadTransfer
 *
 * Runnable transfer moves a file from an external endpoint to this location.
 * A read operation reads a packets from a socket until the transfer is complete
 * and then closes the socket.
 *
 * Example
 * (new Thread(
 *      new ReadTransfer(
 *          ENDPOINT, "myTestFile"
 *      )
 * )).start()
 *
 */
public class ReadTransfer extends Transfer {

    /**
     * Constructs a new transfer that will read data from a socket and
     * store it in a file with the given name
     *
     * @param socket - Socket that this transfer will read from
     * @param filename - Name of file to store incoming data
     */
    public ReadTransfer (NodeSocket socket, String filename){
        super(socket, filename);
    }

    /**
     * Sends a request to this transfer's endpoint to start the transfer
     */
    public void sendRequest () throws IOException {
        this.getSocket().send(new ReadRequest(this.getFilename()));
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
