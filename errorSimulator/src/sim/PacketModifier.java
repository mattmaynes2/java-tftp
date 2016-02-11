package sim;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;
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
	
	private byte[] opCode;
	private byte[] data;
	private String mode;
	private String filename;
	private int blockNum;	
	private int length;
	
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
			return modifyRequestPacket((Request)inMessage, packetIn);
		case ACK:
			return modifyAckPacket((AckMessage)inMessage, packetIn);
		case DATA:
			return modifyDataPacket((DataMessage)inMessage, packetIn);
		default:
			return packetIn;
		}
	}

	private DatagramPacket modifyDataPacket(DataMessage inMessage, DatagramPacket packetIn) {
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
			
			return(handleLength(out, packetIn.getSocketAddress()));
		} catch(IOException e) {
			return new DatagramPacket(inBytes, inBytes.length, packetIn.getSocketAddress());
		}
	}

	private DatagramPacket modifyRequestPacket(Request inMessage, DatagramPacket packetIn) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] inBytes = inMessage.toBytes();
		try {
			if (this.opCode != null) {
				out.write(this.opCode);
			} else {
				out.write(0);
				out.write(inMessage.getOpCode().getCode());
			}
			
			if (this.filename != null) {
				out.write(this.filename.getBytes());
			} else {
				out.write(inMessage.getFilename().getBytes());
			}
			
			out.write(0);
			
			if (this.mode != null) {
				out.write(this.mode.getBytes());
			} else {
				out.write(inMessage.getMode().getBytes());
			}
			
			out.write(0);
			
			return(handleLength(out, packetIn.getSocketAddress()));
		} catch(IOException e) {
			return new DatagramPacket(inBytes, inBytes.length, packetIn.getSocketAddress());
		}
	}

	private DatagramPacket modifyAckPacket(AckMessage inMessage, DatagramPacket packetIn) {
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
			
			return(handleLength(out, packetIn.getSocketAddress()));
		} catch(IOException e) {
			return new DatagramPacket(inBytes, inBytes.length, packetIn.getSocketAddress());
		}
	}
	
	private DatagramPacket handleLength(ByteArrayOutputStream outStream, SocketAddress socket) {

		if (this.length != IGNORE) {
			if(this.length < outStream.size()) {
				byte[] outBytes = outStream.toByteArray();
				return new DatagramPacket(outBytes, this.length, socket);
			}
			while(outStream.size() < this.length) {
				try {
					outStream.write("0".getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			byte[] outBytes = outStream.toByteArray();
			return new DatagramPacket(outBytes, this.length, socket);
		} else {
			byte[] outBytes = outStream.toByteArray();
			return new DatagramPacket(outBytes, outBytes.length, socket);
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
