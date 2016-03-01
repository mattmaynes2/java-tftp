package core.req;

import java.io.ByteArrayOutputStream;

/**
 * This is the base class that holds all common functionality for any message
 *
 */
public abstract class Message {

    /**
     * Operation code describing the purpose of this message
     */
    private OpCode opCode;

    /**
     * Constructs a message by decoding the given byte array
     *
     * @param bytes - Bytes representing a message
     *
     * @throws InvalidMessageException - If the bytes do not form a valid message
     */
    public Message (byte[] bytes) throws InvalidMessageException {
        decode(bytes);
    }

    /**
     * Constructs a message with the desired op code
     *
     *  @param opCode - Type of message
     */
    public Message (OpCode opCode) {
        this.opCode = opCode;
    }

    /**
     * Sets the opcode
     *
     * @param bytes - The byte list containing the opcode
     *
     * @throws InvalidMessageException - If the given bytes do not form a valid messsage
     */
    protected void decode (byte[] bytes) throws InvalidMessageException {
        this.opCode = OpCode.convert(bytes[1]);
    }

    /**
     * Returns the opcode
     *
     * @return getOpCode - The opcode
     */
    public OpCode getOpCode() {
        return this.opCode;
    }

    /**
     * Returns a byte array containing the opcode
     *
     * @return A byte representation of this message
     */
    public byte[] toBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0);
        out.write(getOpCode().getCode());
        return out.toByteArray();
    }

    /**
     * Returns a String representation of the opcode
     */
    public String toString() {
        return "OpCode: " + getOpCode().getCode();
    }
}
