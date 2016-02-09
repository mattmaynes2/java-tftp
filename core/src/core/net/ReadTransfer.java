package core.net;

import core.net.Transfer;
import core.net.NodeSocket;

import core.req.DataMessage;
import core.req.Message;
import core.req.ReadRequest;
import core.req.AckMessage;
import core.req.InvalidMessageException;
import core.req.ErrorMessageException;

import java.io.FileOutputStream;
import java.io.IOException;

import java.net.SocketAddress;
import java.net.SocketException;

import java.util.logging.Logger;

/**
 * Read Transfer
 *
 * Runnable transfer moves a file from an external endpoint to this location.
 * A read operation reads a packets from a socket until the transfer is complete
 *
 * Example
 * (new Thread(
 *      new ReadTransfer(ENDPOINT, "myTestFile")
 * )).start()
 *
 */
public class ReadTransfer extends Transfer {

    /**
     * Constructs a new transfer that will read data from a socket and
     * store it in a file with the given name
     *
     * @param address - Address of endpoint to read from
     * @param filename - Name of file to store incoming data
     *
     * @throws SocketException - If the socket cannot be created
     */
    public ReadTransfer (SocketAddress address, String filename) throws SocketException {
        super(address, filename);
    }

    /**
     * Sends a request to this transfer's endpoint to start the transfer
     *
     * @throws IOException - If the endpoint is not listening or the send fails
     */
    public void sendRequest () throws IOException {
        ReadRequest request = new ReadRequest(this.getFilename());
        notifySendMessage(request);
        this.getSocket().send(request);
    }

    /**
     * Performs a read operation by reading data messages from the socket and
     * writing them to the output file
     */
    public void run () {
        FileOutputStream out;
        DataMessage msg;

        try {
            // Starting the transfer
            this.notifyStart();

            // Create a stream to write the file too
            out = new FileOutputStream(this.getFilename());
            msg = this.getNext();

            // We should continue to read until we get a block
            // that is less than the standard data block size
            while (msg.getData().length == DataMessage.BLOCK_SIZE){

                // Forward the data to the output file
                out.write(msg.getData());

                // Get the next message
                msg = this.getNext();
            }

            // Write the last data message to the file
            out.write(msg.getData());

            // Close the output stream and the socket
            out.close();
            this.getSocket().close();

            // Notify that the transfer is complete
            this.notifyComplete();
        } catch (ErrorMessageException e){
            // TODO Do something here
        } catch (InvalidMessageException e){
            this.handleInvalidMessage(e);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Synchronously receive the next data packet and sends an acknowledgement
     *
     * @return The next data packet read from the socket
     *
     * @throws IOException - If the socket is closed or there is a sending error
     * @throws InvalidMessageException - If there is on error decoding the packet
     */
    private DataMessage getNext () throws IOException, InvalidMessageException, ErrorMessageException {
        Message msg;
        DataMessage data;
        AckMessage ack;

        msg = this.getSocket().receive();
        this.checkMessage(msg);

        data = (DataMessage) msg;
        ack = new AckMessage(data.getBlock());

        this.notifyMessage(data);
        this.notifySendMessage(ack);

        this.getSocket().send(ack);
        return data;
    }

}
