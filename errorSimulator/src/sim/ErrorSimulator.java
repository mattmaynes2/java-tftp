package sim;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;

import core.cli.Command;
import core.ctrl.Controller;
import core.log.Logger;
import core.req.ErrorMessage;
import core.req.Message;

public class ErrorSimulator extends Controller {

    public static final int SIMULATOR_PORT = 68;
    public static final int REQUEST_PACKET = 1;
    
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
    public ErrorSimulator() throws SocketException  {
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
        System.out.println("    op            <packetNumber> <opCode>        Changes the opcode of a specified packet");
        System.out.println("    csa           <packetNumber>                 Changes the sender address of a specified packet");
        System.out.println("    cl            <packetNumber> <packetLength>  Changes the length of a specified packet");
        System.out.println("    rrs           <packetNumber>                 Removes the Request Seperator. ie Removes 0 Byte after Filename");
        System.out.println("    rend                                         Removes the end byte. ie Removes the 0 Byte after Mode");
    }

    /**
     * Starts a new ErrorSimulator thread
     * @param args
     */
    public static void main(String[] args) {
        Logger.init(Level.INFO);
        ErrorSimulator simulator;
        try {
            simulator= new ErrorSimulator();
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
        	this.changeLengthSimulation(command.getArguments());
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
        case END_COMMAND:
        	removeEndByteSimulation();
        }
        
	}
	
	/**
	 * Set the configuration to remove the null at the end of a DatagramPacket
	 */
	private void removeEndByteSimulation() {
		PacketModifier modifier = new PacketModifier();
		modifier.setEndByte(false);
		recieveListener.setConfiguration(SimulationTypes.REPLACE_PACKET, REQUEST_PACKET, modifier);
		this.cli.message("Now running Remove Simulation on incoming requests");		
	}

	/**
	 * Set the configuration to remove the null after the filename
	 */
	private void removeRequestSeperatorSimulation() {
		PacketModifier modifier = new PacketModifier();
		modifier.setPostFilenameByte(false);
		recieveListener.setConfiguration(SimulationTypes.REPLACE_PACKET, REQUEST_PACKET, modifier);
		this.cli.message("Now running Change Sender Address Simulation on incoming requests");		
	}

	/**
	 * Set the configuration to set the response for a specified packet number to be from the wrong address
	 * @param packetNumber
	 */
	private void wrongSocketSimulation(String packetNumber) {
		try {
			recieveListener.setConfiguration(SimulationTypes.CHANGE_SENDER, Integer.parseInt(packetNumber),null);
			this.cli.message("Now running Change Sender Address Simulation on incoming requests");
		}catch(NumberFormatException e) {
			this.cli.message("parameter must be a digit");
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
		try {
			int length = Integer.parseInt(args.get(1));
			PacketModifier modifier = new PacketModifier();
			modifier.setLength(length);
			recieveListener.setConfiguration(SimulationTypes.REPLACE_PACKET, Integer.parseInt(args.get(0)),  modifier);
			this.cli.message("Now running Change Length Simulation on incomming requests");
		} catch(NumberFormatException e) {
			this.cli.message("parameter must be a digit");
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
		
		//validate the opcode is the correct length
		if(opCode.length() != 2) {
			this.cli.message("Incorrect opcode, two digits required.");
			return;
		}
		
		//Parse out the opcode into bytes
		byte[] opCodeBytes = new byte[2];
		String[] opCodeArray = opCode.split("");
		opCodeBytes[0] = Byte.parseByte(opCodeArray[0]);
		opCodeBytes[1] = Byte.parseByte(opCodeArray[1]);
		
		PacketModifier modifier = new PacketModifier();
		modifier.setOpCode(opCodeBytes);
		try {
			recieveListener.setConfiguration(SimulationTypes.REPLACE_PACKET, Integer.parseInt(args.get(0)),  modifier);
			this.cli.message("Now running Change Opcode Simulation on incomming requests");	
		} catch(NumberFormatException e) {
			this.cli.message("parameter must be a digit");
		}
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
