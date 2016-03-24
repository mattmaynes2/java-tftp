package sim;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import core.cli.Command;
import core.ctrl.Controller;
import stream.SimulatorStream;
import stream.SimulatorStreamFactory;
import threads.SimulationEventListener;

public class ErrorSimulator extends Controller implements SimulationEventListener{

    public static final int SIMULATOR_PORT = 68;
    public static final int REQUEST_PACKET = 1;
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
    private static final String DUPLICATE_COMMAND = "duplicate";
    private static final String DROP_COMMAND = "drop";


    private ReceiveWorker recieveListener;
    private int simulationsInProgress = 0;

    /**
     * Add commands to the interpreter
     * @param commandLineArgs - a String array of arguments from the command line call
     * @throws SocketException - a socket exception can occur if there are no ports available
     */
    public ErrorSimulator(String[] commandLineArgs) throws SocketException  {
        super(commandLineArgs);
        recieveListener = new ReceiveWorker(SIMULATOR_PORT);
        recieveListener.subscribeSimulationEvents(this);
        this.interpreter.addCommand(NORMAL_COMMAND);
        this.interpreter.addCommand(OPCODE_COMMAND);
        this.interpreter.addCommand(WRONG_SENDER_COMMAND);
        this.interpreter.addCommand(LENGTH_COMMAND);
        this.interpreter.addCommand(REQUEST_SEPERATOR_COMMAND);
        this.interpreter.addCommand(END_COMMAND);
        this.interpreter.addCommand(MODE_COMMAND);
        this.interpreter.addCommand(DELAY_COMMAND);
        this.interpreter.addCommand(DUPLICATE_COMMAND);
        this.interpreter.addCommand(DROP_COMMAND);
    }

    /**
     * start the ErrorSimulator
     */
    @Override
    public void start() {
        super.start();
        this.passThroughSimulation();
        recieveListener.start();
    }

    /**
     * stop the ErrorSimulator
     */
    @Override
    public void stop() {
        if (simulationsInProgress != 0){
        	this.cli.message("There are currently " + simulationsInProgress + " simulations in progress. The simulator will automatically shut down when they have completed");
        }
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
        System.out.println("<type> must be either 'ack','data', or 'req'");
        System.out.println("<packetNum> starts counting at 1 (The first data packet is data 1)");
        System.out.println("Each simulation runs for a single transfer. The mode reset to norm after each transfer");
        System.out.println("The server and client timeout is " + TIMEOUT_MILLISECONDS  + "ms\n");
        System.out.println("    Commands:");
        System.out.println("    help                                         	Prints this message");
        System.out.println("    shutdown                                     	Exits the simulator");
        System.out.println("    norm                                         	Forward packets through without alteration" );
        System.out.println("    rend                                         	Removes the end byte of the next request packet. ie Removes the 0 Byte after Mode");
        System.out.println("    rrs                                          	Removes the Request Seperator of the next request packet. ie Removes 0 Byte after Filename");
        System.out.println("    mode          <mode>                         	Changes the mode of the next request packet");
        System.out.println("    csa           <type> <packetNum>                    Changes the sender TID of a specified packet");
        System.out.println("    op            <type> <packetNum> <opCode>		Changes the opcode of a specified packet");
        System.out.println("    cl            <type> <packetNum> <packetLen>	Changes the length of a specified packet");
        System.out.println("    delay         <type> <packetNum> <numTimeouts>	Delays the specified packet by a number of timeouts");
        System.out.println("    duplicate     <type> <packetNum>			Sends a duplicate of the specified packet");
        System.out.println("    drop          <type> <packetNum>			Drops the specified packet");
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
           LOGGER.log(Level.SEVERE, "Socket could not bind to port: " + SIMULATOR_PORT);
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
        try{
	        switch (command.getToken()){
	            case NORMAL_COMMAND:
	                this.passThroughSimulation();
	                break;
	            case OPCODE_COMMAND:
	                this.changeOpcodeSimulation(command.getArguments());
	                break;
	            case WRONG_SENDER_COMMAND:
	                try{
	                    wrongSocketSimulation(command.getArguments());
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
	            case DUPLICATE_COMMAND:
	            	this.duplicatePacketSimulation(command.getArguments());
	            	break;
	            case DROP_COMMAND:
	            	this.dropPacketSimulation(command.getArguments());
	        }
        } catch (IllegalArgumentException ex){
        	this.cli.message(ex.getMessage());
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
		if (timeout < 0){
			this.cli.message("Timeout must be a positive number");
			return;
		}
        if(packetNum > 0 && packetNum < HIGHEST_PACKET) {
	    	try {
				SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.DELAY_PACKET, arguments.get(0), packetNum, timeout);
		    	recieveListener.setConfiguration(stream);
		    	this.cli.message(arguments.get(0) + " packet " + packetNum + " will now be delayed by " + timeout + "ms");
			} catch (SocketException e) {
				e.printStackTrace();
			} catch(IllegalArgumentException e){
				this.cli.message(e.getMessage());
			}
        } else {
        	this.cli.message("Packet Number out of bounds:   1 < packetNumber < 65535");
        }

	}

    /**
     * Sets the simulator configuration to delay a packet
     * @param arguments
     */
    private void dropPacketSimulation(ArrayList<String> arguments) {
		if (arguments.size() < 2){
			throw new IllegalArgumentException("Drop simulation requires 2 arguments");
		}
	    int packetNum = verifyNum(arguments.get(1), 1);
        if(packetNum > 0 && packetNum < HIGHEST_PACKET) {
	    	try {
				SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.DROP_PACKET, arguments.get(0), packetNum);
		    	recieveListener.setConfiguration(stream);
		    	this.cli.message(arguments.get(0) + " packet " + packetNum + " will now be dropped");
			} catch (SocketException e) {
				e.printStackTrace();
			} catch(IllegalArgumentException e){
				this.cli.message(e.getMessage());
			}
        } else {
        	this.cli.message("Packet Number out of bounds:   1 < packetNumber < 65535");
        }
	}

    /**
     * Sets the simulator configuration to delay a packet
     * @param arguments
     */
    private void duplicatePacketSimulation(ArrayList<String> arguments) {
		if (arguments.size() < 2){
			throw new IllegalArgumentException("Duplicate simulation requires 3 arguments");
		}
	    int packetNum = verifyNum(arguments.get(1), 1);

        if(packetNum > 0 && packetNum < HIGHEST_PACKET) {
	    	try {
				SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.DUPLICATE_PACKET, arguments.get(0), packetNum);
		    	recieveListener.setConfiguration(stream);
		    	this.cli.message(arguments.get(0) + " packet " + packetNum + " will now be duplicated");
			} catch (SocketException e) {
				e.printStackTrace();
			} catch(IllegalArgumentException e){
				this.cli.message(e.getMessage());
			}
        } else {
        	this.cli.message("Packet Number out of bounds:   1 < packetNumber < 65535");
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
    	this.cli.message("Next request will now pass through unaltered");
	}

    /**
     * Set the configuration to modify the mode of request packets
     * @param mode - the value that mode will be modified to
     */
    private void changeModeSimulation(String mode){
    	PacketModifier modifier = new PacketModifier();
    	modifier.setMode(mode);
		try {
			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.REPLACE_PACKET, "req", REQUEST_PACKET, modifier);
	    	recieveListener.setConfiguration(stream);
		} catch (SocketException e) {
			e.printStackTrace();
		}
    	this.cli.message("Next request will have its mode changed to " + mode);
    }

	/**
     * Set the configuration to remove the null at the end of a DatagramPacket
     */
    private void removeEndByteSimulation() {
        PacketModifier modifier = new PacketModifier();
        modifier.setEndByte(false);
		try {
			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.REPLACE_PACKET, "req", REQUEST_PACKET, modifier);
	    	recieveListener.setConfiguration(stream);
		} catch (SocketException e) {
			e.printStackTrace();
		}
        this.cli.message("Next request will have its End of Message 0 byte removed");
    }

    /**
     * Set the configuration to remove the null after the filename
     */
    private void removeRequestSeperatorSimulation() {
        PacketModifier modifier = new PacketModifier();
        modifier.setPostFilenameByte(false);
		try {
			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.REPLACE_PACKET, "req", REQUEST_PACKET, modifier);
	    	recieveListener.setConfiguration(stream);
		} catch (SocketException e) {
			e.printStackTrace();
		}
        this.cli.message("Next request will have its 0 byte seperator removed");
    }

    /**
     * Set the configuration to set the response for a specified packet number to be from the wrong address
     * @param packetNumber - value that the packet number will be modified to
     */
    private void wrongSocketSimulation(ArrayList<String> args) {
    	int packetNum = verifyNum(args.get(1), 1);
    	if(packetNum > 0 && packetNum < HIGHEST_PACKET) {
    		try {
    			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.CHANGE_SENDER, args.get(0), packetNum);
    	    	recieveListener.setConfiguration(stream);
    		} catch (SocketException e) {
    			e.printStackTrace();
    		}
    		this.cli.message("Packet " + packetNum + " will now be sent from the wrong sender");
    	}
        else {
        	this.cli.message("Packet Number out of bounds:   1 < packetNumber < 65535");
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

        // Try to get the packet number from the string, then form the packet modifier, setting the new length
        int length = verifyNum(args.get(2), 1);
        int packetNum = verifyNum(args.get(1), 1);
        if(packetNum > 0 && packetNum < HIGHEST_PACKET) {
	        PacketModifier modifier = new PacketModifier();
	        modifier.setLength(length);
    		try {
    			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.REPLACE_PACKET, args.get(0), packetNum,  modifier);
    	    	recieveListener.setConfiguration(stream);
    		} catch (SocketException e) {
    			e.printStackTrace();
    		}
	        this.cli.message("Packet " + packetNum + " will now have its length changed to " + length);
        }
        else {
        	this.cli.message("Packet Number out of bounds:   1 < packetNumber < 65535");
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

        int packetNum = verifyNum(args.get(1), 1);
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
	    			SimulatorStream stream = SimulatorStreamFactory.createSimulationStream(SimulationTypes.REPLACE_PACKET, args.get(0), packetNum,  modifier);
	    	    	recieveListener.setConfiguration(stream);
	    		} catch (SocketException e) {
	    			e.printStackTrace();
	    		}
	            this.cli.message("Packet " + packetNum + " will now have its opcode changed to " + opCodeInt);
	        }
	        else {
	        	this.cli.message("OpCode out of bounds.  -32727 < opcode < 32728");
	        }
        }
        else {
        	this.cli.message("Packet Number out of bounds:   1 <= packetNumber < 65535");
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
			return -1;
		}
		return returnNum;
    }

	@Override
	public synchronized void simulationStarted() {
		this.simulationsInProgress += 1;
	}

	@Override
	public synchronized void simulationComplete() {
		this.simulationsInProgress -= 1;

	}
}
