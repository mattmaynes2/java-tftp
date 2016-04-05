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

    /**
     * Data block payload size. All datagram packet will contain
     * this block size worth of bytes (not including the header bytes)
     */
    public static final int BLOCK_SIZE = 512;

    /**
     * Payload for this message
     */
    private byte[] data;

    /**
     * Constructs a data message using the given encoded bytes
     *
     * @param bytes - Array of byte data to decode
     *
     * @throws InvalidMessageException - If the bytes do no produce a valid Data Message
     */
    public DataMessage(byte[] bytes) throws InvalidMessageException {
        super(bytes);
    }

    /**
     * Constructs a data message for the given block number which will
     * contain the desired payload
     *
     * @param block - Index of this data block in a transfer sequence
     * @param data  - Payload of data to transfer
     */
    public DataMessage (short block, byte[] data) {
        super(OpCode.DATA,block);
        this.data = data;
    }

    /**
     * The method verifies the byte list is in a valid form for a data acknowledgement packet
     * If verified, it stores the data in a private byte array
     *
     * @param bytes - A byte list to decode
     *
     * @throws InvalidMessageException - If the given bytes do not produce a valid data message
     */
    @Override
    protected void decode (byte[] bytes) throws InvalidMessageException {
        if (bytes.length >= ACK_SIZE && bytes.length <= BLOCK_SIZE+ACK_SIZE) {
            super.decode(Arrays.copyOfRange(bytes, 0, ACK_SIZE));
            this.data=Arrays.copyOfRange(bytes, ACK_SIZE, bytes.length);

        } else if (bytes.length < ACK_SIZE) {
            throw new InvalidMessageException("Data Message must be at least " + ACK_SIZE + " bytes");
        } else {
            throw new InvalidMessageException("Data Message must be less than " +
                    (BLOCK_SIZE + ACK_SIZE + 1) + "bytes");
        }
    }

    /**
     * Writes the data to a byte stream and returns it
     *
     * @return An encoded version of this data message
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
     * Returns a byte array of the data contained in the message
     * @return Payload of this message
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Returns if this message is the last one in a transfer sequence
     *
     * @return If block is last in sequence
     */
    public boolean isLastBlock () {
        return this.data.length < BLOCK_SIZE;
    }
}
