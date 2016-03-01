
package core.req;

import core.req.Message;
import core.req.OpCode;
import core.req.RequestMode;

import core.util.ByteUtils;

import java.util.Arrays;

import java.io.ByteArrayOutputStream;

/**
 * Defines functionality for a request message
 *
 */
public abstract class Request extends Message {

    /**
     * Mode to run transfer in (either "OCTET" or "NETASCII")
     */
    private RequestMode mode;

    /**
     * Name of file to transfer
     */
    private String filename;

    /**
     * Sets the request information
     *
     * @param code - The opcode
     *
     * @param filename - The file name
     */
    public Request (OpCode code, String filename) {
        super(code);
        this.mode = RequestMode.BINARY;
        this.filename = filename;
    }

    /**
     * Sets the request information passed in as a byte array
     *
     * @param data - The byte array
     *
     * @throws InvalidMessageException - If the given byte data does not form a valid request
     */
    public Request (byte[] data) throws InvalidMessageException {
        super(data);
    }

    /**
     * Sets the transfer mode
     *
     * @param mode - The transfer mode
     */
    public void setMode (RequestMode mode){
        this.mode = mode;
    }

    /**
     * Gets the transfer mode
     *
     * @return The transfer mode
     */
    public RequestMode getMode () {
        return this.mode;
    }

    /**
     * Sets the filename
     *
     * @param filename - Path and name of file to transfer
     */
    public void setFilename (String filename){
        this.filename = filename;
    }

    /**
     * Gets the filename
     *
     * @return Path and name of file to transfer
     */
    public String getFilename () {
        return this.filename;
    }

    /**
     * The method verifies the byte list is in a valid form for a request packet
     * If verified, it decodes the filename and transfer mode into Strings that are locally stored
     *
     * @param data - A byte list to decode
     */
    @Override
    protected void decode (byte[] data) throws InvalidMessageException {
        int fileIndex, modeIndex;

        super.decode(data);

        fileIndex = ByteUtils.indexOf(data, 2, (byte) 0x00);
        this.filename = new String(Arrays.copyOfRange(data, 2, fileIndex));

        modeIndex = ByteUtils.indexOf(data, fileIndex + 1, (byte) 0x00);
        if(modeIndex < 0) {
        	throw new InvalidMessageException("Missing End 0 Byte.");
        }
    	String tempMode = new String(Arrays.copyOfRange(data, fileIndex+1, modeIndex));

        try{
        	this.mode = RequestMode.convert(tempMode);
        }catch(IllegalArgumentException ex){
        	throw new InvalidMessageException("Invalid mode " + tempMode);
        }

    }

    /**
     * Writes the data to a byte stream and returns it as a byte array
     *
     * @return An encoded version of this request
     */
    public byte[] toBytes () {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            buffer.write(super.toBytes());
            buffer.write(this.filename.getBytes());
            buffer.write(0x00);
            buffer.write(this.mode.getBytes());
            buffer.write(0x00);
        } catch (Exception e){
            // This only happens if we run out of memory
            e.printStackTrace();
            System.exit(1);
        }

        return buffer.toByteArray();
    }

    /**
     * Returns a string representation of the data contained in the message
     */
    public String toString() {
        return super.toString() +
            "\nMode: " + this.mode +
            "\nFile: " + this.filename;
    }

}
