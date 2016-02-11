package core.net;

import core.req.Message;
import core.req.WriteRequest;
import core.req.DataMessage;
import core.req.AckMessage;
import core.req.InvalidMessageException;
import core.req.ErrorMessageException;
import core.req.OpCode;

import java.io.FileInputStream;
import java.io.IOException;

import java.net.SocketAddress;
import java.net.SocketException;

import java.util.Arrays;

/**
 * Write Transfer
 *
 * Runnable transfer moves a file from this location to an external endpoint.
 * A write operation chunks a file into block sized packets and writes them
 * to a socket.
 *
 * Example
 * ( new Thread(
 *      new WriteTransfer(ENDPOINT, "myTestFile")
 * )).start()
 */
public class WriteTransfer extends Transfer {

    /**
     * Current data block being transferred
     */
    private short currentBlock;

    /**
     * Constructs a new transfer will write data to a socket from
     * the file with the given name
     *
     * @param address - Address of endpoint to send data
     * @param filename - Name of file to send to server
     *
     * @throws SocketException - If the socket cannot be created
     */
    public WriteTransfer (SocketAddress address, String filename) throws SocketException {
        super(address, filename);
        this.currentBlock = 0;
    }

    /**
     * Sends a request to start this transfer
     *
     * @throws IOException - If the endpoint is not listening or the write fails
     */
    public void sendRequest () throws IOException {
        WriteRequest request = new WriteRequest(this.getFilename());
        notifySendMessage(request);
        this.getSocket().send(request);
        this.getSocket().reset();
    }

    /**
     * Performs a write operation by taking block sized chunks from a file
     * and writing them to the socket
     */
    public void run () {
        FileInputStream in;
        DataMessage msg;

        try {
            // Starting the transfer
            this.notifyStart();

            // Create a new stream to read the file
            in = new FileInputStream(this.getFilename());

            // Continue to send data until all of the data has been sent
            do {
                msg = createMessage(in);
                this.sendDataMessage(msg);
                this.notifyMessage(this.getAcknowledge());
            } while(msg.getData().length == 512);

            // Close the input stream and socket
            in.close();
            this.getSocket().close();

            // Notify that the transfer is complete
            this.notifyComplete();
        } catch (ErrorMessageException e) {
            // TODO Do something
        } catch (InvalidMessageException e) {
            this.handleInvalidMessage(e);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Synchronously blocks and waits for an acknowledgment from the
     * socket endpoint
     *
     * @return An acknowledge message
     *
     * @throws IOException - If the socket is closed
     * @throws InvalidMessageException - If the received message has an invalid encoding
     */
    public AckMessage getAcknowledge () throws IOException, InvalidMessageException, ErrorMessageException {
        Message msg;

        msg = this.getSocket().receive();
        this.checkMessage(msg);
        this.checkCast(msg, OpCode.ACK);
        return (AckMessage) msg;
    }

    /**
     * Sends a data message to the endpoint
     *
     * @param msg - Message to send to endpoint
     */
    private void sendDataMessage(DataMessage msg) throws IOException {
        this.notifySendMessage(msg);
        this.getSocket().send(msg);
    }

    /**
     * Creates a data message out of the next block in the given input stream
     *
     * @param in - The input stream it read the next block from
     */
    private DataMessage createMessage (FileInputStream in) throws IOException {
        DataMessage message;
        int read;
        byte[] data;

        // Increment the block
        this.currentBlock++;
        data = new byte[DataMessage.BLOCK_SIZE];
        read = in.read(data);

        // If there are only 0 bytes read in then we need to send
        // a data packet with no data. Otherwise we will truncate the
        // data to only use the data read in
        if (read >= 0) {
            message = new DataMessage(this.currentBlock, Arrays.copyOfRange(data, 0, read));
        }
        else {
            message = new DataMessage(this.currentBlock, new byte[0]);
        }

        return message;
    }

}
