package core.req;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import core.util.ByteUtils;

/**
 * This class is responsible for data packets that are treated as acknowledgments 
 *
 */
public class DataMessage extends AckMessage {

    private byte[] data;
    public DataMessage(byte[] bytes) throws InvalidMessageException {
        super(bytes);
    }

    public DataMessage (short block, byte[] data) {
        super(OpCode.DATA,block);
        this.data=data;
    }

    /** 
     * @param bytes  a byte list to decode
     * The method verifies the byte list is in a valid form for a data acknowledgement packet
     * If verified, it stores the data in a private byte array
     */
    @Override
    protected void decode(byte[] bytes) throws InvalidMessageException {
        super.decode(bytes);
        if(bytes.length>4) {
            this.data=Arrays.copyOfRange(bytes, 4, bytes.length);
        }else {
            this.data=null;
        }
    }

    /**
     * Writes the data to a byte stream and returns it
     */
    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(super.toBytes());
            if(data!=null) {
                out.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * Returns a string representation of the data contained in the message
     */
    @Override
    public String toString() {
        return super.toString() + ByteUtils.bytesToHexString(this.data);
    }

    /**
     * Returns a byte array of the data contained in the message
     * @return
     */
    public byte[] getData() {
        return data;
    }
}
