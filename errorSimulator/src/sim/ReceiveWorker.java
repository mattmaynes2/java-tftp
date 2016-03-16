package sim;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import core.util.Worker;
import stream.PacketStream;
import stream.SimulatorStream;
import threads.SimulationEventListener;
import threads.SimulatorThread;

public class ReceiveWorker extends Worker{

	private DatagramSocket requestSocket;
	private SimulatorStream stream;
	private SimulationEventListener simulationListener;
	
	public ReceiveWorker(int port) throws SocketException {
		requestSocket = new DatagramSocket(port);
		stream = null;
	}
	
	@Override
	public void execute() {
         DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
         try {
			requestSocket.receive(receivePacket);
			SimulatorThread newThread = new SimulatorThread(receivePacket, stream);
			if (simulationListener != null){
				newThread.subscribeSimulationEvents(simulationListener);
			}
			newThread.start();
			stream = new PacketStream();
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
		stream.close();
	}
	
	public void setConfiguration(SimulatorStream stream) {
		this.stream = stream;
	}
	
	public void subscribeSimulationEvents(SimulationEventListener listener){
		this.simulationListener = listener;
	}
}
