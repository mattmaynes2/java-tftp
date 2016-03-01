package core.req;

/**
 * Enum containing the valid byte possibilities of an opcode, and some functionality associated to them.
 *
 */
public enum OpCode {
    READ    ((byte) 0x01),
    WRITE   ((byte) 0x02),
    DATA    ((byte) 0x03),
    ACK     ((byte) 0x04),
    ERROR   ((byte) 0x05);

    private byte code;

    /**
     * Sets the opcode byte
     *
     * @param code  the byte to set the opcode
     */
    OpCode (byte code) {
        this.code = code;
    }

    /**
     * Returns the opcode byte
     *
     * @return code  the opcode byte
     */
    public byte getCode(){
        return this.code;
    }

    /**
     * Returns the opcode enum based on the opcode byte
     *
     * @param code - The opcode byte
     *
     * @return the opcode enum
     *
     * @throws InvalidMessageException - If the given byte is not a valid op code
     */
    public static OpCode convert(byte code) throws InvalidMessageException {
    	if (code < 1 || code > OpCode.values().length){
    		throw new InvalidMessageException("Invalid opcode");
    	}
    	return OpCode.values()[code - 1];
    }
}
