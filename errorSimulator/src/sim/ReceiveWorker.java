package sim;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import sim.SimulationTypes;
import core.util.Worker;
import threads.SimulatorThread;
import stream.SimulatorStream;

public class ReceiveWorker extends Worker {

	private DatagramSocket requestSocket;
	private SimulatorStream stream;
	
	public ReceiveWorker(int port) throws SocketException {
		requestSocket = new DatagramSocket(port);
		stream = null;
	}
	
	@Override
	public void execute() {
         DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
         try {
			requestSocket.receive(receivePacket);
			(new SimulatorThread(receivePacket, stream)).start();
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
	
	public void setConfiguration(SimulatorStream stream) {
		this.stream = stream;
	}

}
