package core.req;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import core.util.ByteUtils;

public class DataMessage extends AckMessage {

    private byte[] data;
    public DataMessage(byte[] bytes) throws InvalidMessageException {
        super(bytes);
    }

    public DataMessage (int block, byte[] data) {
        super(OpCode.DATA,block);
        this.data=data;
    }

    @Override
    protected void decode(byte[] bytes) throws InvalidMessageException {
        super.decode(bytes);
        if(bytes.length>4) {
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
        return super.toString() + ByteUtils.bytesToHexString(this.data);
    }

    public byte[] getData() {
        return data;
    }
}
