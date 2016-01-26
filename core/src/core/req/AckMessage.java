package core.req;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import core.util.ByteUtils;


public class AckMessage extends Message {

    private int block;

    public AckMessage(byte[] bytes) throws InvalidMessageException {
        super(bytes);
    }

    public AckMessage(int block){
        super(OpCode.ACK);
        this.block=block;
    }

    /**
     * Constructor used by classes that extend AckMessage
     */
    protected AckMessage(OpCode opcode,int block) {
        super(opcode);
        this.block=block;
    }

    public int getBlock() {
        return this.block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    @Override
    public String toString() {
        return super.toString()+" block number: "+this.block;
    }

    @Override
    protected void decode(byte[] bytes) throws InvalidMessageException {
        if (bytes.length<4){
            throw new InvalidMessageException("Ack Message must be 4 bytes");
        }
        super.decode(bytes);
        this.block= ByteUtils.bytesToInt(Arrays.copyOfRange(bytes, 2, 4));
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(super.toBytes());
            out.write(ByteUtils.intToByteArray(block));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
