package core.req;

/**
 * Factory that creates a specific type of Message based on an opcode byte
 *
 */
public class MessageFactory {

    public static Message createMessage(byte[] bytes) throws InvalidMessageException {
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
     * @param message
     * @return True if it is the last block in a data message.  Otherwise False.
     */
    public static boolean isLastMessage(Message message) {
        if(message.getOpCode().equals(OpCode.DATA)) {
            return (((DataMessage) message).isLastBlock());
        }
        else {
            return false;
        }

    }
}
