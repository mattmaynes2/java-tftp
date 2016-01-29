package core.req;

import java.io.ByteArrayOutputStream;

/**
 * This is the base class that holds all common functionality for any message
 *
 */
public abstract class Message {

    private OpCode opCode;

    public Message(byte[] bytes) throws InvalidMessageException {
        decode(bytes);
    }

    public Message(OpCode opCode) {
        this.opCode = opCode;
    }

    /**
     * Sets the opcode
     * @param bytes  the byte list containing the opcode
     * @throws InvalidMessageException
     */
    protected void decode(byte[] bytes) throws InvalidMessageException {
        this.opCode = OpCode.convert(bytes[1]);
    }

    /**
     * Returns the opcode
     * @return getOpCode  the opcode
     */
    public OpCode getOpCode() {
        return this.opCode;
    }

    /**
     * Returns a byte array containing the opcode 
     * @return
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
