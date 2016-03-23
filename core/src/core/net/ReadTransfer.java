package core.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;

import core.req.AckMessage;
import core.req.DataMessage;
import core.req.ErrorCode;
import core.req.ErrorMessage;
import core.req.ErrorMessageException;
import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageOrderException;
import core.req.OpCode;
import core.req.ReadRequest;

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
     * @param destinationName - Destination path of the file to transfer
     *
     * @throws SocketException - If the socket cannot be created
     */
    public ReadTransfer (SocketAddress address, String filename, String destinationName) throws SocketException {
        super(address, filename, destinationName);
    }

    /**
     * Sends a request to this transfer's endpoint to start the transfer
     *
     * @throws IOException - If the endpoint is not listening or the send fails
     *
     * @return If the request was accepted
     */
    public boolean sendRequest () throws IOException {
        ReadRequest request = new ReadRequest(this.getFilename());
        this.notifySendMessage(request);
        this.getSocket().send(request);
        this.getSocket().reset();
        return true;
    }

    /**
     * Performs a read operation by reading data messages from the socket and
     * writing them to the output file
     */
    public void run () {
        FileOutputStream out = null;
        DataMessage msg;

        // Starting the transfer
        this.notifyStart();

        try {
            msg = this.getNext();
            // Create a stream to write the file too
            out = new FileOutputStream(this.destinationName);
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

            // Wait until we are certain the last ack message was received by the server
            this.waitAfterLastAck();

            // Close the output stream and the socket
            out.close();
            this.getSocket().close();

            // Notify that the transfer is complete
            this.notifyComplete();
        } catch (ErrorMessageException e){
            this.notifyError(e.getErrorMessage());
            this.removeFile(out);
        } catch (InvalidMessageException e){
            this.handleInvalidMessage(e);
            this.removeFile(out);
        } catch (UnreachableHostException e) {
            this.notifyException(e);
            this.removeFile(out);
        } catch (IOException e) {
        	this.handleDiskFull();
        	this.removeFile(out);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Delete the file if a problem occurs during transfer
     *
     * @param out - the outputstream for the file that needs to be deleted
     */
    private void removeFile (FileOutputStream out) {
        if (out != null) {
            try {
                out.close();
                File f = new File(this.getFilename());
                if (f.delete()) {
                    this.notifyInfo("Deleted " + getFilename());
                } else {
                    this.notifyInfo("Unable to deleted " + getFilename());
                }
            } catch (IOException e1) {
                this.notifyInfo("Error closing output stream");
            }
        }
    }

    /**
     * Synchronously receive the next data packet and sends an acknowledgement
     *
     * @return The next data packet read from the socket
     *
     * @throws IOException - If the socket is closed or there is a sending error
     * @throws InvalidMessageException - If there is on error decoding the packet
     * @throws UnreachableHostException - If the max number of attempts is reached when receiving a packet
     * @throws ErrorMessageException - If an error message is received
     */
    private DataMessage getNext () throws
        IOException,
        InvalidMessageException,
        ErrorMessageException,
        UnreachableHostException {

        Message msg = null;
        DataMessage data = null;
        AckMessage ack;

        msg = this.getSocket().receive();

        // Check that the message is not an error message
        this.checkErrorMessage(msg);

        // Increment the block number
        this.incrementBlockNumber();
        // Check that we can cast the message to the type if the
        // desired OpCode
        this.checkCast(msg, OpCode.DATA);
        data = (DataMessage) msg;

        // Ensure that the packet we got is in the correct
        // sequence with previous packets we have received
        try {
            this.checkOrder(data);
        } catch (MessageOrderException e) {

            this.notifyInfo(e.getMessage() + "\n resending ack");

            // Reset block number to before message was received
            this.decrementBlockNumber();

            if (Short.toUnsignedInt(getBlockNumber()) > 0) {

                // Resend last acknowledge
                ack = new AckMessage(this.getBlockNumber());
                this.notifySendMessage(ack);
                this.getSocket().send(ack);
                return this.getNext();
            }
        }

        ack = new AckMessage(data.getBlock());

        // Notify the listeners that a message we received successfully
        // and that we are sending the next acknowledgement
        this.notifyMessage(data);
        this.notifySendMessage(ack);

        // Send the ACK
        this.getSocket().send(ack);
        return data;
     }

    /**
     * There is the possibility that the last ack sent gets dropped,
     * so the transfer should not end until the server receives it
     *
     * @throws IOException - If the socket is closed or there is a sending error
     * @throws InvalidMessageException - If there is on error decoding the packet
     * @throws ErrorMessageException - If an error message is received
     */
    private void waitAfterLastAck() throws
        IOException,
        InvalidMessageException,
        ErrorMessageException {
        Message msg = null;
        AckMessage ack;

        try {
            this.getSocket().setAttempts(2);
            msg = this.getSocket().receive();
            this.notifyMessage(msg);
        } catch (UnreachableHostException e) {
            // Squash IT!!!
        }

        if (msg != null) {

            // Check that the message is not an error message
            this.checkErrorMessage(msg);

            // Notify the listeners that we are re-sending the ack message
            this.notifyInfo("Received data packet again, resending ack");

            // Re-send ack message
            ack = new AckMessage(this.getBlockNumber());
            this.notifySendMessage(ack);
            this.getSocket().send(ack);
        }

    }

    /**
     * Handles an I/O exception where the disk is full or the disk allocation
     * has exceeded by sending an error message on the socket and notifies
     * listeners of the error
     */
    private void handleDiskFull() {
    	ErrorMessage msg;
        try {
            msg = new ErrorMessage(ErrorCode.DISK_FULL, "Disk is full.");
            this.getSocket().send(msg);
            this.notifyError(msg);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
