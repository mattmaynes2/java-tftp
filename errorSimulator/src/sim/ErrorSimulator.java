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
    
    /**
     * Command to initialize a menu command
     */
    public static final String OPCODE_COMMAND = "change-opcode";
    
    private ReceiveWorker recieveListener;

    public ErrorSimulator() throws SocketException  {
        recieveListener = new ReceiveWorker(SIMULATOR_PORT);
        this.interpreter.addCommand(OPCODE_COMMAND);
    }

    public void handleComplete () {}

    public void handleMessage(Message msg){}
    
    public void handleStart () {}

    @Override
    public void start() {
        super.start();
        recieveListener.start();
    }

    @Override
    public void stop() {
        super.stop();
        recieveListener.stop();
        recieveListener.teardown();
    }

    @Override
    public void usage() {
        System.out.println("TFTP Error Simulator");
        System.out.println("  Commands:");
        System.out.println("    help           Prints this message");
        System.out.println("    shutdown       Exits the simulator");
        System.out.println("    change-opcode  Changes the opcode of a packet");
    }

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

    @Override
    public void handleSendMessage(Message msg) {}

	@Override
	public void handleErrorMessage(ErrorMessage err) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void handleCommand(Command command) {
		super.handleCommand(command);
        switch (command.getToken()){
        case OPCODE_COMMAND:
            this.changeOpcode(command.getArguments());
            break;
        }
	}
	
	public void changeOpcode(ArrayList<String> args) {

		if(args.size() != 2) {
			this.cli.message("Incorrect number of parameters for change-code.  Format is change-opcode packetNumber newOpcode");
			return;
		}
		
		String opCode = args.get(1);
		
		if(opCode.length() != 2) {
			this.cli.message("Incorrect opcode, two digits required.");
			return;
		}
		byte[] opCodeBytes = new byte[2];
		String[] opCodeArray = opCode.split("");
		opCodeBytes[0] = Byte.parseByte(opCodeArray[0]);
		opCodeBytes[1] = Byte.parseByte(opCodeArray[1]);
		
		PacketModifier modifier = new PacketModifier();
		modifier.setOpCode(opCodeBytes);
		recieveListener.setConfiguration(SimulationTypes.REPLACE_PACKET, Integer.parseInt(args.get(0)),  modifier);
		Logger.log(Level.INFO,"Running change Opcode Simulation on next request");
		
	}

}
