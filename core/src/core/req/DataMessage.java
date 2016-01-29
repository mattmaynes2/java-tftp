package core.req;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import core.util.ByteUtils;

public class DataMessage extends AckMessage {

    /**
     * Data block payload size. All datagram packet will contain
     * this block size worth of bytes (not including the header bytes)
     */
    public static final int BLOCK_SIZE = 512;

    private byte[] data;
    public DataMessage(byte[] bytes) throws InvalidMessageException {
        super(bytes);
    }

    public DataMessage (short block, byte[] data) {
        super(OpCode.DATA,block);
        this.data=data;
    }

    @Override
    protected void decode(byte[] bytes) throws InvalidMessageException {
        super.decode(bytes);
        if(bytes.length>=4) {
            this.data=Arrays.copyOfRange(bytes, 4, bytes.length);
        }else {
            this.data=null;
        }
    }

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

    @Override
    public String toString() {
        return super.toString() + " data: " + Arrays.toString(this.data);
    }

    public byte[] getData() {
        return data;
    }

    public boolean isLastBlock () {
        return this.data.length < BLOCK_SIZE;
    }
}
