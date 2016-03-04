package core.net;

import core.req.AckMessage;
import core.req.DataMessage;
import core.req.Message;
import core.req.ReadRequest;
import core.req.OpCode;
import core.req.ErrorMessageException;
import core.req.InvalidMessageException;
import core.req.MessageOrderException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

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
     *
     * @return If the request was accepted
     */
    public boolean sendRequest () throws IOException {
        ReadRequest request = new ReadRequest(this.getFilename());
        notifySendMessage(request);
        this.getSocket().send(request);
        this.getSocket().reset();
        return true;
    }

    /**
     * Performs a read operation by reading data messages from the socket and
     * writing them to the output file
     */
    public void run () {
        FileOutputStream out;
        DataMessage msg;

        // Starting the transfer
        this.notifyStart();

        try {
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
            this.notifyError(e.getErrorMessage());
        } catch (InvalidMessageException e){
            this.handleInvalidMessage(e);
        }catch(UnreachableHostException e) {
        	this.notifyException(e);
        }catch (Exception e){
            e.printStackTrace();
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
     */
    private DataMessage getNext () throws
            IOException,
            InvalidMessageException,
            ErrorMessageException,
            UnreachableHostException {

        Message msg=null;
        DataMessage data = null;
        AckMessage ack;
        int sendAttemps=0;
      while(msg == null) {
      		try {
      			msg = this.getSocket().receive();
      	}catch(SocketTimeoutException e){
      		sendAttemps++;
      		if(sendAttemps == MAX_ATTEMPTS) {
      			throw new UnreachableHostException("No response from host tried 5 times");
      		}
      		this.notifyTimeout(MAX_ATTEMPTS-sendAttemps);
      		//don't try and send ack after missing response after request
      		if(Short.toUnsignedInt(getBlockNumber())>0) {
      			//re-send last ack
      			ack = new AckMessage(this.getBlockNumber());
      			this.notifySendMessage(ack);
      			this.getSocket().send(ack);
      		}
      		
      		}
      		if(msg!=null) {
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
		        }catch(MessageOrderException e) {
		        	
		        	this.notifyInfo(e.getMessage()+"\nIgnoring Message");
		        	//reset block number to ignore message
		        	this.decrementBlockNumber();
		        	//reset data
		        	msg=null;
		        }
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

}
