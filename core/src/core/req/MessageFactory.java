package core.req;

/**
 * Factory that creates a specific type of Message based on an opcode byte
 *
 */
public class MessageFactory {
	
	public static Message createMessage(byte[] bytes) throws InvalidMessageException {
		if (bytes[0] != 0) {
			throw new InvalidMessageException("The first byte of the opCode is not 0.");
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
			default:
				throw new InvalidMessageException("The second byte of the opCode does not represent a valid message.");		
		}
	}
}
