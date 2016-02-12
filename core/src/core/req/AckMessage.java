package core.req;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This class is responsible for handling acknowledge packets
 *
 */
public class AckMessage extends Message {

    protected static final int ACK_SIZE = 4;
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

    /**
     * 
     * @return the block number 
     */
    public short getBlock() {
        return this.block;
    }

    /**
     * set the block number
     * @param block
     */
    public void setBlock(short block) {
        this.block = block;
    }

    /**
     * Returns a string representation of the block number
     */
    @Override
    public String toString() {
        return super.toString()+" block number: "+this.block;
    }

    /** 
     * Param bytes  a byte list to decode
     * The method verifies the byte list is in a valid form for an acknowledgement packet
     * If verified, it sets the block number to the appropriate value  
     */
    @Override
    protected void decode(byte[] bytes) throws InvalidMessageException {
        if (bytes.length != ACK_SIZE){
            throw new InvalidMessageException("Ack Message must be 4 bytes");
        }
        super.decode(bytes);
        
        //stores the block number in a byte buffer to be converted into a short
        ByteBuffer wrapper = ByteBuffer.wrap(bytes, 2, 2);
        
        this.block= wrapper.getShort();
    }

    /**
     * Converts the opcode to a byte array and returns it
     */
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
