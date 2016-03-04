package core.req;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This class is responsible for handling acknowledge packets
 *
 */
public class AckMessage extends Message {

    /**
     * Number of bytes in an acknowledge message
     */
    protected static final int ACK_SIZE = 4;

    /**
     * Block number being acknowledged
     */
    private short block;

    /**
     * Constructs an acknowledge message byte decoding the given byte data
     *
     * @param bytes - Encoded message to decode
     *
     * @throws InvalidMessageException - Thrown if the byte data does not represent a valid message
     */
    public AckMessage(byte[] bytes) throws InvalidMessageException {
        super(bytes);
        this.decode(bytes);
    }

    /**
     * Constructs an acknowledge message for the given block
     *
     * @param block - Index of block being acknowledged
     */
    public AckMessage(short block){
        super(OpCode.ACK);
        this.block=block;
    }

    /**
     * Constructor used by classes that extend AckMessage
     *
     * @param opcode - Code for acknowledge message
     * @param block - Block number being acknowledged
     */
    protected AckMessage(OpCode opcode, short block) {
        super(opcode);
        this.block=block;
    }

    /**
     * Returns the block number of the acknowledge
     *
     * @return the block number
     */
    public short getBlock() {
        return this.block;
    }

    /**
     * set the block number
     * @param block - Block number for this packet
     */
    public void setBlock(short block) {
        this.block = block;
    }

    /**
     * Returns a string representation of the block number
     */
    @Override
    public String toString() {
        return super.toString()+" block number: "+Short.toUnsignedInt(block);
    }

    /**
     * The method verifies the byte list is in a valid form for an acknowledgement packet
     * If verified, it sets the block number to the appropriate value
     *
     * @param bytes - List of bytes to decode
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
