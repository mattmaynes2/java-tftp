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
	
	private DatagramPacket packetIn;
	private Message messageIn;
	private byte[] inBytes;
	
	private byte[] opCode;
	private byte[] data;
	private String mode;
	private String filename;
	private int blockNum;	
	private int length;
	private boolean postFilenameByte;
	private boolean endByte;

	
	public PacketModifier () {
		opCode = null;
		data = null;
		mode = null;
		filename = null;
		blockNum = IGNORE;	
		length = IGNORE;
		postFilenameByte = true;
		endByte = true;
	}
	
	public DatagramPacket modifyPacket(DatagramPacket packet) throws InvalidMessageException {		
		packetIn = packet;
		messageIn = MessageFactory.createMessage(inBytes);
		inBytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
		OpCode inOpCode = messageIn.getOpCode();
		switch(inOpCode) {
		case READ:
		case WRITE:
			return modifyRequestPacket((Request)messageIn, packetIn);
		case ACK:
			return modifyAckPacket((AckMessage)messageIn, packetIn);
		case DATA:
			return modifyDataPacket((DataMessage)messageIn, packetIn);
		default:
			return packetIn;
		}
	}

	private DatagramPacket modifyDataPacket(DataMessage inMessage, DatagramPacket packetIn) {
		byte[] inBytes = inMessage.toBytes();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write(handleOpCode());
			out.write(handleBlockNum());
			
			if (this.data != null) {
				out.write(this.data);
			} else {
				out.write(Arrays.copyOfRange(inBytes, DATA_INDEX, inBytes.length));
			}
			
			return(handleLength(out));
		} catch(IOException e) {
			return new DatagramPacket(inBytes, inBytes.length, packetIn.getSocketAddress());
		}
	}

	private DatagramPacket modifyRequestPacket(Request inMessage, DatagramPacket packetIn) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] inBytes = inMessage.toBytes();
		try {
			out.write(handleOpCode());
			
			if (this.filename != null) {
				out.write(this.filename.getBytes());
			} else {
				out.write(inMessage.getFilename().getBytes());
			}
			
			if(postFilenameByte) {
				out.write(0);
			}
			
			if (this.mode != null) {
				out.write(this.mode.getBytes());
			} else {
				out.write(inMessage.getMode().getBytes());
			}
			
			if(endByte) {
				out.write(0);
			}
			
			return(handleLength(out));
		} catch(IOException e) {
			return new DatagramPacket(inBytes, inBytes.length, packetIn.getSocketAddress());
		}
	}

	private DatagramPacket modifyAckPacket(AckMessage inMessage, DatagramPacket packetIn) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] inBytes = inMessage.toBytes();
		try {
			out.write(handleOpCode());
			out.write(handleBlockNum());			
			return(handleLength(out));
		} catch(IOException e) {
			return new DatagramPacket(inBytes, inBytes.length, packetIn.getSocketAddress());
		}
	}
	
	private DatagramPacket handleLength(ByteArrayOutputStream outStream) throws IOException {

		if (this.length != IGNORE) {
			if(this.length < outStream.size()) {
				byte[] outBytes = outStream.toByteArray();
				return new DatagramPacket(outBytes, this.length, packetIn.getSocketAddress());
			}
			while(outStream.size() < this.length) {
				outStream.write("0".getBytes());
			}
			byte[] outBytes = outStream.toByteArray();
			return new DatagramPacket(outBytes, this.length, packetIn.getSocketAddress());
		} else {
			byte[] outBytes = outStream.toByteArray();
			return new DatagramPacket(outBytes, outBytes.length, packetIn.getSocketAddress());
		}
	}
	
	private byte[] handleOpCode() throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		if (this.opCode != null) {
			outStream.write(this.opCode);
		} else {
			outStream.write(0);
			outStream.write(messageIn.getOpCode().getCode());
		}
		return outStream.toByteArray();
	}
	
	private byte[] handleBlockNum() throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		if (this.blockNum != IGNORE) {
			outStream.write(this.blockNum);
		} else {
			outStream.write(Arrays.copyOfRange(inBytes, BLOCK_NUM_INDEX, DATA_INDEX));
		}
		return outStream.toByteArray();
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
	public void setPostFilenameByte(boolean postFilenameByte) {
		this.postFilenameByte = postFilenameByte;
	}
	public void setEndByte(boolean endByte) {
		this.endByte = endByte;
	}
}
