package sim;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;

import core.cli.Command;
import core.ctrl.Controller;
import core.log.Logger;
import core.req.ErrorMessage;
import core.req.Message;

public class ErrorSimulator extends Controller {

    public static final int SIMULATOR_PORT = 68;
    public static final int REQUEST_PACKET = 0;
    public static final short LOWEST_SHORT = (short) -32727;
    public static final short HIGHEST_SHORT = (short) 32728;

    /**
     * Declare valid commands as static final
     */
    private static final String NORMAL_COMMAND = "norm";
    private static final String OPCODE_COMMAND = "op";
    private static final String WRONG_SENDER_COMMAND = "csa";
    private static final String LENGTH_COMMAND = "cl";
    private static final String REQUEST_SEPERATOR_COMMAND = "rrs";
    private static final String END_COMMAND = "rend";

    private ReceiveWorker recieveListener;

    /**
     * Add commands to the interpreter
     * @throws SocketException
     */
    public ErrorSimulator(String[] commandLineArgs) throws SocketException  {
        super(commandLineArgs);
        recieveListener = new ReceiveWorker(SIMULATOR_PORT);
        this.interpreter.addCommand(NORMAL_COMMAND);
        this.interpreter.addCommand(OPCODE_COMMAND);
        this.interpreter.addCommand(WRONG_SENDER_COMMAND);
        this.interpreter.addCommand(LENGTH_COMMAND);
        this.interpreter.addCommand(REQUEST_SEPERATOR_COMMAND);
        this.interpreter.addCommand(END_COMMAND);
    }

    /**
     * start the ErrorSimulator
     */
    @Override
    public void start() {
        super.start();
        recieveListener.start();
    }

    /**
     * stop the ErrorSimulator
     */
    @Override
    public void stop() {
        super.stop();
        recieveListener.stop();
        recieveListener.teardown();
    }

    /**
     * Display the valid commands, their parameters, and a brief description of their functionality
     */
    @Override
    public void usage() {
        System.out.println("TFTP Error Simulator");
        System.out.println("    Commands:");
        System.out.println("    help                                         Prints this message");
        System.out.println("    shutdown                                     Exits the simulator");
        System.out.println("    norm                                         Forward packets through without alteration" );
        System.out.println("    rend                                         Removes the end byte. ie Removes the 0 Byte after Mode");
        System.out.println("    rrs                                          Removes the Request Seperator. ie Removes 0 Byte after Filename");
        System.out.println("    csa           <packetNumber>                 Changes the sender address of a specified packet");
        System.out.println("    op            <packetNumber> <opCode>        Changes the opcode of a specified packet");
        System.out.println("    cl            <packetNumber> <packetLength>  Changes the length of a specified packet");
    }

    /**
     * Starts a new ErrorSimulator thread
     * @param args
     */
    public static void main(String[] args) {
        ErrorSimulator simulator;
        try {
            simulator= new ErrorSimulator(args);
            simulator.start();
        } catch (SocketException e) {
            Logger.log(Level.SEVERE, "Socket could not bind to port: " + SIMULATOR_PORT);
        }
    }

    /**
     * Invoked when a user types a command on the interface
     *
     * @param command - User's CLI command
     */
    @Override
    public void handleCommand(Command command) {
        super.handleCommand(command);
        switch (command.getToken()){
            case NORMAL_COMMAND:
                this.passThroughSimulation();
                break;
            case OPCODE_COMMAND:
                this.changeOpcodeSimulation(command.getArguments());
                break;
            case WRONG_SENDER_COMMAND:
                try{
                    wrongSocketSimulation(command.getFirstArgument());
                }catch (IndexOutOfBoundsException e) {
                    this.cli.message("Incorrect number of parameters for wrong-sender.  Format is wrong-sender packetNumber");
                }
                break;
            case LENGTH_COMMAND:
                this.changeLengthSimulation(command.getArguments());
                break;
            case REQUEST_SEPERATOR_COMMAND:
                removeRequestSeperatorSimulation();
                break;
            case END_COMMAND:
                removeEndByteSimulation();
                break;
        }

    }

    /**
     * Set the configuration back to pass through
     */
    private void passThroughSimulation() {
    	recieveListener.setConfiguration(SimulationTypes.PASS_THROUGH, 0, null);
    	this.cli.message("Incoming requests are now passing through unaltered");		
	}

	/**
     * Set the configuration to remove the null at the end of a DatagramPacket
     */
    private void removeEndByteSimulation() {
        PacketModifier modifier = new PacketModifier();
        modifier.setEndByte(false);
        recieveListener.setConfiguration(SimulationTypes.REPLACE_PACKET, REQUEST_PACKET, modifier);
        this.cli.message("Now running Remove End Byte Simulation on incoming requests");
    }

    /**
     * Set the configuration to remove the null after the filename
     */
    private void removeRequestSeperatorSimulation() {
        PacketModifier modifier = new PacketModifier();
        modifier.setPostFilenameByte(false);
        recieveListener.setConfiguration(SimulationTypes.REPLACE_PACKET, REQUEST_PACKET, modifier);
        this.cli.message("Now running Remove Request Seperator Simulation on incoming requests");
    }

    /**
     * Set the configuration to set the response for a specified packet number to be from the wrong address
     * @param packetNumber
     */
    private void wrongSocketSimulation(String packetNumber) {
    	int packetNum = verifyNum(packetNumber, 1);
    	if(packetNum > 0) {
    		recieveListener.setConfiguration(SimulationTypes.CHANGE_SENDER, packetNum,null);
    		this.cli.message("Now running Change Sender Address on incoming requests");
    	}
    }

    /**
     * Change the length of a packet
     * @param args  Will contain the packet number to change, and the length to change it to
     */
    private void changeLengthSimulation(ArrayList<String> args) {
        //args must be of size two
        if(args.size() != 2) {
            this.cli.message("Incorrect number of parameters for change-length.  Format is change-length packetNumber newLength");
            return;
        }

        // Try to get the packet number from the string, then form the packet modifier, setting the new length
        int length = verifyNum(args.get(1), 0);
        int packetNum = verifyNum(args.get(0), 1);
        if(packetNum > 0) {
	        PacketModifier modifier = new PacketModifier();
	        modifier.setLength(length);
	        recieveListener.setConfiguration(SimulationTypes.REPLACE_PACKET, packetNum,  modifier);
	        this.cli.message("Now running Change Length Simulation on incoming requests");
        }
    }

    /**
     * Change the op code of a packet
     * @param args  Will contain the packet number to change, and the opcode to change it to
     */
    private void changeOpcodeSimulation(ArrayList<String> args) {
        //args must be size of two
        if(args.size() != 2) {
            this.cli.message("Incorrect number of parameters for change-code.  Format is change-opcode packetNumber newOpcode");
            return;
        }
        //get the opcode
        String opCode = args.get(1);
        
        int packetNum = verifyNum(args.get(0), 0);
        if(packetNum >= 0) {
	        //Parse out the opcode into bytes
	        short opCodeInt = (short)verifyNum(opCode, LOWEST_SHORT);
	        if(LOWEST_SHORT < opCodeInt && opCodeInt < HIGHEST_SHORT) {    
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            ByteBuffer b = ByteBuffer.allocate(2);
	    	    b.putShort(opCodeInt);
	    	
	    	    byte[] result = b.array();
		        try {
					out.write(result);
				} catch (IOException e) {
					e.printStackTrace();
				}
		        
		        PacketModifier modifier = new PacketModifier();
		        modifier.setOpCode(out.toByteArray());
	            recieveListener.setConfiguration(SimulationTypes.REPLACE_PACKET, packetNum,  modifier);
	            this.cli.message("Now running Change Opcode Simulation on incoming requests");
	        }
	        this.cli.message("OpCode out of bounds.  -32727 < opcode < 32728");
        }
    }
    
    /**
     * Verifies that a packet number meets the requirements
     * @param packetNum  the number to check 
     * @param min  the minimum allowable value
     * @return
     */
    private int verifyNum(String packetNum, int min) {
    	int returnNum = -1;
    	try { 
    		returnNum = Integer.parseInt(packetNum);
    	} catch (NumberFormatException e){
    		this.cli.message("Parameter must be a digit"); 
    		return -1;
    	}
		if (returnNum <= min-1) {
			this.cli.message("Parameter must be greater than or equal to " + min);
			return -1;
		}
		return returnNum;
    }

    /**
     * unused
     */
    @Override
    public void handleSendMessage(Message msg) {}

    /**
     * unused
     */
    @Override
    public void handleErrorMessage(ErrorMessage err) {}

    /**
    * unused
    */
    public void handleComplete () {}

    /**
    * unused
    */
    public void handleMessage(Message msg){}

    /**
     * unused
     */
    public void handleStart () {}

}
