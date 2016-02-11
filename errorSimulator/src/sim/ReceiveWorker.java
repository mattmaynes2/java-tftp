package sim;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import core.util.Worker;
import threads.SimulationTypes;
import threads.SimulatorThread;


public class ReceiveWorker extends Worker {

	private DatagramSocket requestSocket;
	private SimulationTypes type;
	private int packetNumber;
	private DatagramPacket changePacket;
	private PacketModifier modifier;
	
	public ReceiveWorker(int port) throws SocketException {
		requestSocket = new DatagramSocket(port);
		type = SimulationTypes.PASS_THROUGH;
		packetNumber = 0;
		changePacket = null;
		modifier = null;
	}
	
	@Override
	public void execute() {
         DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
         try {
			requestSocket.receive(receivePacket);
			(new SimulatorThread(receivePacket, type, packetNumber, this.modifier)).start();
		} catch (SocketException e) {
			// Ignore socket exception if not currently running
			if(this.isRunning()) {
				e.printStackTrace();
			}
		} catch (IOException e) {	
			e.printStackTrace();
		}
         
	}
	
	@Override
	public void setup() {}

	@Override
	public void teardown() {
		requestSocket.close();
	}
	
	public void setConfiguration(SimulationTypes type, int packetNumber, PacketModifier modifier) {
		
		this.type = type;
		this.packetNumber = packetNumber;
		this.modifier = modifier;
		
	}

}
