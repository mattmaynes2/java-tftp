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

/**
 * This is responsible for taking a configuration, and modifying a datagram packet accordingly
 */
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
    private byte[] blockNum;
    private int length;
    private boolean postFilenameByte;
    private boolean endByte;


    public PacketModifier () {
        opCode = null;
        data = null;
        mode = null;
        filename = null;
        blockNum = null;
        length = IGNORE;
        postFilenameByte = true;
        endByte = true;
    }

    /**
     * Determines the message type of a given datagram packet, and calls the appropriate modify method
     * @param packet  the packet received
     * @return the new DatagramPacket that was modified
     * @throws InvalidMessageException - an invalid message exception can occur if packet is in an improper format
     */
    public DatagramPacket modifyPacket(DatagramPacket packet) throws InvalidMessageException {
        packetIn = packet;
        inBytes = Arrays.copyOfRange(packetIn.getData(), 0, packetIn.getLength());
        messageIn = MessageFactory.createMessage(inBytes);
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

    /**
     * Modifies a data packet according to the passed in configuration
     * @param inMessage  the message passed in
     * @param packetIn  the packet passed in
     * @return
     */
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

    /**
     * Modifies a request packet according to the passed in configuration
     * @param inMessage  the message passed in
     * @param packetIn  the packet passed in
     * @return
     */
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

    /**
     * Modifies an acknowledgement packet according to the passed in configuration
     * @param inMessage  the message passed in
     * @param packetIn  the packet passed in
     * @return
     */
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

    /**
     * Check to see if the length must be changed.  If so, change it
     * @param outStream  the current output stream to be converted into a byte array
     * @return
     * @throws IOException
     */
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

    /**
     * Check to see if the opcode must be changed.  If so, change it
     * @return
     * @throws IOException
     */
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

    /**
     * Check to see if the block number must be changed.  If so, change it
     * @return
     * @throws IOException
     */
    private byte[] handleBlockNum() throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        if (this.blockNum != null) {
            outStream.write(this.blockNum);
        } else {
            outStream.write(Arrays.copyOfRange(inBytes, BLOCK_NUM_INDEX, DATA_INDEX));
        }
        return outStream.toByteArray();
    }

    /**
     * set what the modifier will change the packet's opCode to
     * @param opCode - byte array that the opCode will be changed to
     */
    public void setOpCode(byte[] opCode) {
        this.opCode = opCode;
    }

    /**
     * set what the modifier will change the packet's data to
     * @param data - byte array that the data will be changed to
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * set what the modifier will change the packet's mode to
     * @param mode - String that the mode will be changed to
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * set what the modifier will change the packet's filename to
     * @param filename - String that the filename will be changed to
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * set what the modifier will change the packet's block number to
     * @param blockNum - integer that the blockNum will be changed to
     */
    public void setBlockNum(byte[] blockNum) {
        this.blockNum = blockNum;
    }

    /**
     * set what the modifier will change the packet's length to
     * @param length - integer that the packet length will be changed to
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * set whether the modifier will add the 0 byte to the packet after the filename or not
     * @param postFilenameByte - boolean that signifies adding the 0 byte or not
     */
    public void setPostFilenameByte(boolean postFilenameByte) {
        this.postFilenameByte = postFilenameByte;
    }

    /**
     * set whether the modifier will add the 0 byte to the packet after the data or not
     * @param endByte - boolean that signifies adding the 0 byte or not
     */
    public void setEndByte(boolean endByte) {
        this.endByte = endByte;
    }
}
