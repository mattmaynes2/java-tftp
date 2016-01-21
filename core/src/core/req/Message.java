package core.req;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public abstract class Message {
	
	private OpCode opCode;
	
	public Message(byte[] bytes) {
		
	}
	
	public Message(OpCode opCode) {
		this.opCode = opCode;
	}
	
	protected void decode(byte[] bytes) {
		
	}
	
	@Override
	public abstract String toString();
	
	public OpCode getOpCode() {
		return this.opCode;
	}
	
	public byte[] toBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(0);
		out.write(getOpCode().getCode());
		return out.toByteArray();
	}
}
