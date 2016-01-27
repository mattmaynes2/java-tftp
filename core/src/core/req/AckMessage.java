package core.req;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AckMessage extends Message {

    private short block;

    public AckMessage(byte[] bytes) throws InvalidMessageException {
        super(bytes);
        this.decode(bytes);
    }

    public AckMessage(short block){
        super(OpCode.ACK);
        this.block=block;
    }

    /**
     * Constructor used by classes that extend AckMessage
     */
    protected AckMessage(OpCode opcode,short block) {
        super(opcode);
        this.block=block;
    }

    public short getBlock() {
        return this.block;
    }

    public void setBlock(short block) {
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
        
        ByteBuffer wrapper = ByteBuffer.wrap(bytes, 2, 4);
        
        this.block= wrapper.getShort();
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteBuffer b = ByteBuffer.allocate(2);
	    b.putShort(this.block);
	
	    byte[] result = b.array();
	      
        try {
            out.write(super.toBytes());
            out.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
