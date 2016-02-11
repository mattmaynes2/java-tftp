package sim;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

import core.req.AckMessage;
import core.req.DataMessage;
import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageFactory;
import core.req.OpCode;
import core.req.Request;

public class PacketModifier {
	
	private static final int BLOCK_NUM_INDEX = 2;
	private static final int DATA_INDEX = 4;
	private static final int IGNORE = -1;
	
	byte[] opCode;
	byte[] data;
	String mode;
	String filename;
	int blockNum;	
	int length;
	
	public PacketModifier () {
		opCode = null;
		data = null;
		mode = null;
		filename = null;
		blockNum = IGNORE;	
		length = IGNORE;
	}
	
	public DatagramPacket modifyPacket(DatagramPacket packetIn) throws InvalidMessageException {
		byte[] inBytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
		Message inMessage;
		inMessage = MessageFactory.createMessage(inBytes);
		OpCode inOpCode = inMessage.getOpCode();
		switch(inOpCode) {
		case READ:
		case WRITE:
			return modifyRequestPacket((Request)inMessage);
		case ACK:
			return modifyAckPacket((AckMessage)inMessage);
		case DATA:
			return modifyDataPacket((DataMessage)inMessage);
		default:
			return packetIn;
		}
	}

	private DatagramPacket modifyDataPacket(DataMessage inMessage) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] inBytes = inMessage.toBytes();
		try {
			if (this.opCode != null) {
				out.write(this.opCode);
			} else {
				out.write(0);
				out.write(inMessage.getOpCode().getCode());
			}
			
			if (this.blockNum != IGNORE) {
				out.write(this.blockNum);
			} else {
				out.write(Arrays.copyOfRange(inBytes, BLOCK_NUM_INDEX, DATA_INDEX));
			}
			
			if (this.data != null) {
				out.write(this.data);
			} else {
				out.write(Arrays.copyOfRange(inBytes, DATA_INDEX, inBytes.length));
			}
			
			byte[] outBytes = out.toByteArray();
			if (this.length != IGNORE) {
				return new DatagramPacket(outBytes, this.length);
			} else {
				return new DatagramPacket(outBytes, outBytes.length);
			}
		} catch(IOException e) {
			return new DatagramPacket(inBytes, inBytes.length);
		}
	}

	private DatagramPacket modifyRequestPacket(Request inMessage) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] inBytes = inMessage.toBytes();
		try {
			if (this.opCode != null) {
				out.write(this.opCode);
			} else {
				out.write(0);
				out.write(inMessage.getOpCode().getCode());
			}
			
			if (!this.filename.equals(null)) {
				out.write(this.filename.getBytes());
			} else {
				out.write(inMessage.getFilename().getBytes());
			}
			
			if (this.mode != null) {
				out.write(this.mode.getBytes());
			} else {
				out.write(inMessage.getMode().getBytes());
			}
			//TODO: maybe?
			byte[] outBytes = out.toByteArray();
			if (this.length != IGNORE) {
				return new DatagramPacket(outBytes, this.length);
			} else {
				return new DatagramPacket(outBytes, outBytes.length);
			}
		} catch(IOException e) {
			return new DatagramPacket(inBytes, inBytes.length);
		}
	}

	private DatagramPacket modifyAckPacket(AckMessage inMessage) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] inBytes = inMessage.toBytes();
		try {
			if (this.opCode != null) {
				out.write(this.opCode);
			} else {
				out.write(0);
				out.write(inMessage.getOpCode().getCode());
			}
			
			if (this.blockNum != IGNORE) {
				out.write(this.blockNum);
			} else {
				out.write(Arrays.copyOfRange(inBytes, BLOCK_NUM_INDEX, DATA_INDEX));
			}
			
			byte[] outBytes = out.toByteArray();
			if (this.length != IGNORE) {
				return new DatagramPacket(outBytes, this.length);
			} else {
				return new DatagramPacket(outBytes, outBytes.length);
			}
		} catch(IOException e) {
			return new DatagramPacket(inBytes, inBytes.length);
		}
	}
	
	public void setOpCode(byte[] opCode) {
		this.opCode = opCode;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public void setBlockNum(int blockNum) {
		this.blockNum = blockNum;
	}
	public void setLength(int length) {
		this.length = length;
	}
}
