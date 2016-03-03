package core.net;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;


import core.req.AckMessage;
import core.req.DataMessage;
import core.req.ErrorMessageException;
import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageOrderException;
import core.req.OpCode;
import core.req.WriteRequest;

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
    }

    /**
     * Sends a request to start this transfer
     *
     * @throws IOException - If the endpoint is not listening or the write fails
     *
     * @return If the request was accepted
     * @throws UnreachableHostException 
     */
    public boolean sendRequest () throws IOException {
        WriteRequest request;

        try {
            request = new WriteRequest(this.getFilename());
            this.notifySendMessage(request);
            this.getSocket().send(request);
            this.getSocket().reset();
            AckMessage ack =null;
            int sendAttemps=0;
            while(ack == null) {
            	try {
            		ack=this.getAcknowledge();
            	}catch(SocketTimeoutException e) {
            		sendAttemps++;
            		if(sendAttemps == MAX_ATTEMPTS) {
            			throw new UnreachableHostException("No response from host tried 5 times");
            		}
            		this.notifyTimeout(MAX_ATTEMPTS-sendAttemps);
            	}
            }
            
        } catch (InvalidMessageException e) {
            this.handleInvalidMessage(e);
            return false;
        } catch (ErrorMessageException e) {
            this.notifyError(e.getErrorMessage());
            return false;
        } catch (MessageOrderException e){
            // This should never happen since it was just a request
            this.notifyException(e);
            return false;
        }catch(UnreachableHostException e) {
        	this.notifyException(e);
            return false;
        }
        return true;
    }

    /**
     * Performs a write operation by taking block sized chunks from a file
     * and writing them to the socket
     */
    public void run () {
        FileInputStream in;
        DataMessage msg;

        // Starting the transfer
        this.notifyStart();

        try {

            // Create a new stream to read the file
            in = new FileInputStream(this.getFilename());

            // Continue to send data until all of the data has been sent
            do {
            	msg = createMessage(in);
            	int sendAttemps=0;
            	AckMessage ack=null;
            	this.sendDataMessage(msg);
            	while(ack == null ) {
	            	try {
	                	ack=this.getAcknowledge();
	            	}catch(SocketTimeoutException e){
	            		sendAttemps++;
	            		if(sendAttemps == MAX_ATTEMPTS) {
	            			throw new UnreachableHostException("No response from host tried 5 times");
	            		}
	            		this.notifyTimeout(MAX_ATTEMPTS-sendAttemps);
	            		//resend message
	            		this.sendDataMessage(msg);
	            	}catch(MessageOrderException e) {
	            		this.notifyInfo(e.getMessage()+"\nIgnoring Messge");
	            	}
            	}
                this.notifyMessage(ack);
            } while(msg.getData().length == 512);

            // Close the input stream and socket
            in.close();
            this.getSocket().close();

            // Notify that the transfer is complete
            this.notifyComplete();
        } catch (ErrorMessageException e) {
            this.notifyError(e.getErrorMessage());
        } catch (InvalidMessageException e) {
            this.handleInvalidMessage(e);
        } catch(UnreachableHostException e) {
        	this.notifyException(e);
        } catch (Exception e){
            e.printStackTrace();
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
     * @throws ErrorMessageException - If an error message is received
     * @throws MessageOrderException - If an acknowledge is received out of order
     * @throws SocketTimeoutException - If an acknowledge packet isn't received within TIMEOUT_TIME
     */
    public AckMessage getAcknowledge () throws
            IOException,
            InvalidMessageException,
            ErrorMessageException,
            MessageOrderException, SocketTimeoutException{

        Message msg;
        AckMessage ack;
        msg = this.getSocket().receive();

        this.checkErrorMessage(msg);
        this.checkCast(msg, OpCode.ACK);
        ack = (AckMessage) msg;
        this.checkOrder(ack);

        return ack;
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
        this.incrementBlockNumber();
        data = new byte[DataMessage.BLOCK_SIZE];
        read = in.read(data);

        // If there are only 0 bytes read in then we need to send
        // a data packet with no data. Otherwise we will truncate the
        // data to only use the data read in
        if (read >= 0) {
            message = new DataMessage(this.getBlockNumber(), Arrays.copyOfRange(data, 0, read));
        }
        else {
            message = new DataMessage(this.getBlockNumber(), new byte[0]);
        }

        return message;
    }

}
