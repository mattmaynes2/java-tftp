package core.req;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import core.util.ByteUtils;


public class AckMessage extends Message {

	private int blockNum;
	
	public AckMessage(byte[] bytes) throws InvalidMessageException {
		super(bytes);
	}

	public AckMessage(int blockNum){
		super(OpCode.ACK);
		this.blockNum=blockNum;
	}
	 
	/**
	 * Constructor used by classes that extend AckMessage
	 */
	protected AckMessage(OpCode opcode,int blockNum) {
		super(opcode);
		this.blockNum=blockNum;
	}
	@Override
	public String toString() {
		return super.toString()+" block number: "+this.blockNum;
	}
	
	@Override
	protected void decode(byte[] bytes) throws InvalidMessageException {
		if (bytes.length<4){
			throw new InvalidMessageException("Ack Message must be 4 bytes");
		}
		super.decode(bytes);
		this.blockNum= ByteUtils.bytesToInt(Arrays.copyOfRange(bytes, 2, 4));
	}
	
	@Override
	public byte[] toBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write(super.toBytes());
			out.write(ByteUtils.intToByteArray(blockNum));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}
}
