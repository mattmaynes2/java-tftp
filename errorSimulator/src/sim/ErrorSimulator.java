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
import stream.SimulatorStream;
import stream.SimulatorStreamFactory;

public class ErrorSimulator extends Controller {

    public static final int SIMULATOR_PORT = 68;
    public static final int REQUEST_PACKET = 0;
    public static final int HIGHEST_PACKET = Short.MAX_VALUE*2 + 1;
    public static final int TIMEOUT_MILLISECONDS = 2400;

    /**
     * Declare valid commands as static final
     */
    private static final String NORMAL_COMMAND = "norm";
    private static final String OPCODE_COMMAND = "op";
    private static final String WRONG_SENDER_COMMAND = "csa";
    private static final String LENGTH_COMMAND = "cl";
    private static final String REQUEST_SEPERATOR_COMMAND = "rrs";
    private static final String END_COMMAND = "rend";
    private static final String MODE_COMMAND = "mode";
    private static final String DELAY_COMMAND = "delay";

    private ReceiveWorker recieveListener;

    /**
     * Add commands to the interpreter
     * @param commandLineArgs - a String array of arguments from the command line call
     * @throws SocketException - a socket exception can occur if there are no ports available
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
        this.interpreter.addCommand(MODE_COMMAND);
        this.interpreter.addCommand(DELAY_COMMAND);
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
        System.out.println("    help                                         	Prints this message");
        System.out.println("    shutdown                                     	Exits the simulator");
        System.out.println("    norm                                         	Forward packets through without alteration" );
        System.out.println("    rend                                         	Removes the end byte. ie Removes the 0 Byte after Mode");
        System.out.println("    rrs                                          	Removes the Request Seperator. ie Removes 0 Byte after Filename");
        System.out.println("    mode          <mode>                         	Changes the mode of a request");
        System.out.println("    csa           <packetNum>                    	Changes the sender address of a specified packet");
        System.out.println("    op            <type> <packetNum> <opCode>		Changes the opcode of a specified packet");
        System.out.println("    cl            <type> <packetNum> <packetLen>	Changes the length of a specified packet");
        System.out.println("    delay <packetType> <packetNumber> <timeouts> Delays the specified packet by a number of timeouts. Timeout is " + TIMEOUT_MILLISECONDS  + "ms");
    }

    /**
     * Starts a new ErrorSimulator thread
     * @param args - String array of arguments received from the Run Configurations
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
            case MODE_COMMAND:
            	try{
            		this.changeModeSimulation(command.getFirstArgument());
            	}catch(IndexOutOfBoundsException e){
            		this.cli.message("Incorrect number of parameters for mode. Format is mode <mode>");
            	}
            	break;
            case DELAY_COMMAND:
            	this.delayPacketSimulation(command.getArguments());
            	break;
        }
    }

    /**
     * Sets the simulator configuration to delay a packet
     * @param arguments
     */
    private void delayPacketSimulation(ArrayList<String> arguments) {
		if (arguments.size() < 3){
			throw new IllegalArgumentException("Delay simulation requires 3 arguments");
		}
	    int packetNum = verifyNum(arguments.get(1), 1);
		int timeout = Integer.parseInt(arguments.get(2)) * TIMEOUT_MILLISECONDS;
    	try {
			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.DELAY_PACKET, arguments.get(0), packetNum, timeout);
	    	recieveListener.setConfiguration(stream);
	    	this.cli.message(arguments.get(0) + " packet " + packetNum + " will now be delayed by " + timeout + "ms");
		} catch (SocketException e) {
			e.printStackTrace();
		} catch(IllegalArgumentException e){
			this.cli.message(e.getMessage());
		}
	}

	/**
     * Set the configuration back to pass through
     */
    private void passThroughSimulation() {
		try {
			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.PASS_THROUGH);
	    	recieveListener.setConfiguration(stream);
		} catch (SocketException e) {
			e.printStackTrace();
		}
    	this.cli.message("Incoming requests are now passing through unaltered");		
	}

    /**
     * Set the configuration to modify the mode of request packets
     * @param mode - the value that mode will be modified to
     */
    private void changeModeSimulation(String mode){
    	PacketModifier modifier = new PacketModifier();
    	modifier.setMode(mode);
		try {
			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.REPLACE_PACKET, REQUEST_PACKET, modifier);
	    	recieveListener.setConfiguration(stream);
		} catch (SocketException e) {
			e.printStackTrace();
		}
    	this.cli.message("Incoming request packets will have their mode changed to " + mode);
    }
    
	/**
     * Set the configuration to remove the null at the end of a DatagramPacket
     */
    private void removeEndByteSimulation() {
        PacketModifier modifier = new PacketModifier();
        modifier.setEndByte(false);
		try {
			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.REPLACE_PACKET, REQUEST_PACKET, modifier);
	    	recieveListener.setConfiguration(stream);
		} catch (SocketException e) {
			e.printStackTrace();
		}
        this.cli.message("Now running Remove End Byte Simulation on incoming requests");
    }

    /**
     * Set the configuration to remove the null after the filename
     */
    private void removeRequestSeperatorSimulation() {
        PacketModifier modifier = new PacketModifier();
        modifier.setPostFilenameByte(false);
		try {
			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.REPLACE_PACKET, REQUEST_PACKET, modifier);
	    	recieveListener.setConfiguration(stream);
		} catch (SocketException e) {
			e.printStackTrace();
		}
        this.cli.message("Now running Remove Request Seperator Simulation on incoming requests");
    }

    /**
     * Set the configuration to set the response for a specified packet number to be from the wrong address
     * @param packetNumber - value that the packet number will be modified to
     */
    private void wrongSocketSimulation(String packetNumber) {
    	int packetNum = verifyNum(packetNumber, 1);
    	if(packetNum > 0 && packetNum < HIGHEST_PACKET) {
    		try {
    			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.CHANGE_SENDER, packetNum);
    	    	recieveListener.setConfiguration(stream);
    		} catch (SocketException e) {
    			e.printStackTrace();
    		}
    		this.cli.message("Now running Change Sender Address on incoming requests");
    	}
        else {
        	this.cli.message("Packet Number out of bounds:   0 < packetNumber < 65535");
        }
    	
    }

    /**
     * Change the length of a packet
     * @param args  Will contain the packet type to change, its packet number, and the length to change it to
     */
    private void changeLengthSimulation(ArrayList<String> args) {
        //args must be of size three
        if(args.size() != 3) {
            this.cli.message("Incorrect number of parameters for cl.  Format is cl <type> <packetNum> <newLen>");
            return;
        }
        
        //get the simulation type
        SimulationTypes type = determineSimulationType(args.get(0));
        
        if (type == SimulationTypes.PASS_THROUGH) {
        	return;
        }
        
        // Try to get the packet number from the string, then form the packet modifier, setting the new length
        int length = verifyNum(args.get(2), 0);
        int packetNum = verifyNum(args.get(1), 1);
        if(packetNum > 0 && packetNum < HIGHEST_PACKET) {
	        PacketModifier modifier = new PacketModifier();
	        modifier.setLength(length);
    		try {
    			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(type, args.get(0), packetNum,  modifier);
    	    	recieveListener.setConfiguration(stream);
    		} catch (SocketException e) {
    			e.printStackTrace();
    		}
	        this.cli.message("Now running Change Length Simulation on incoming requests");
        }
        else {
        	this.cli.message("Packet Number out of bounds:   0 < packetNumber < 65535");
        }
    }

    /**
     * Change the op code of a packet
     * @param args  Will contain the packet type to change, its packet number, and the opcode to change it to
     */
    private void changeOpcodeSimulation(ArrayList<String> args) {
        //args must be size of three
        if(args.size() != 3) {
            this.cli.message("Incorrect number of parameters for op.  Format is op <packetNum> <opCode> <type>");
            return;
        }
        
        //get the opcode
        String opCode = args.get(2);
        
        //get the simulation type
        SimulationTypes type = determineSimulationType(args.get(0).toUpperCase());
        
        if (type == SimulationTypes.PASS_THROUGH) {
        	return;
        }
        
        int packetNum = verifyNum(args.get(1), 0);
        if(packetNum >= 0 && packetNum < HIGHEST_PACKET) {
	        //Parse out the opcode into bytes
	        short opCodeInt = (short)verifyNum(opCode, Short.MIN_VALUE);
	        if(Short.MIN_VALUE < opCodeInt && opCodeInt < Short.MAX_VALUE) {    
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
		 		try {
	    			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(type, packetNum,  modifier);
	    	    	recieveListener.setConfiguration(stream);
	    		} catch (SocketException e) {
	    			e.printStackTrace();
	    		}
	            this.cli.message("Now running Change Opcode Simulation on incoming requests");
	        }
	        else {
	        	this.cli.message("OpCode out of bounds.  -32727 < opcode < 32728");
	        }
        }
        else {
        	this.cli.message("Packet Number out of bounds:   0 <= packetNumber < 65535");
        }
    }
    
    /**
     * Determine the SimulationType from the string argument
     */
    private SimulationTypes determineSimulationType(String type) {
    	if (type.equals("ACK")) {
        	return SimulationTypes.REPLACE_ACK;
        } else if (type.equals("DATA")) {
        	return SimulationTypes.REPLACE_DATA;
        } else if (type.equals("REQ")){
        	return SimulationTypes.REPLACE_PACKET;
        } else {
        	this.cli.message("Incorrect packet type parameter. Options are ACK, DATA, or REQ");
        	return SimulationTypes.PASS_THROUGH;
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
}
