package core.req;

/**
 * Factory that creates a specific type of Message based on an opcode byte
 *
 */
public class MessageFactory {

    /**
     * Creates a message by decoding the given byte data
     *
     * @param bytes - An encoded version of a message
     *
     * @throws InvalidMessageException - If the given bytes do not form a valid message
     */
    public static Message createMessage(byte[] bytes) throws InvalidMessageException {
    	if (bytes.length < 2) {
    		throw new InvalidMessageException("Invalid opCode: requires two bytes");
    	}

        if (bytes[0] != 0) {
            throw new InvalidMessageException("Invalid first byte: expected 0x00 got " + bytes[0]);
        }

        // Takes the opcode byte and runs a switch on it, throws an exception if it is not a valid opcode
        switch(OpCode.convert(bytes[1])) {
            case READ:
                return new ReadRequest(bytes);
            case WRITE:
                return new WriteRequest(bytes);
            case DATA:
                return new DataMessage(bytes);
            case ACK:
                return new AckMessage(bytes);
            case ERROR:
                return new ErrorMessage(bytes);
            default:
                throw new InvalidMessageException("Invalid message op code: " + bytes[1]);
        }
    }

    /**
     * Checks to see if the message is a data message, and if so, if it is the last block
     *
     * @param message - Message to check
     *
     * @return True if it is the last block in a data message .Otherwise False.
     */
    public static boolean isLastMessage(Message message) {
        if(OpCode.DATA.equals(message.getOpCode())) {
            return (((DataMessage) message).isLastBlock());
        }
        else {
            return  OpCode.ERROR.equals(message.getOpCode());
        }

    }
}
