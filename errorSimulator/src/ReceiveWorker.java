import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import core.util.Worker;

public class ReceiveWorker extends Worker {

	private DatagramSocket requestSocket;
	
	public ReceiveWorker(int port) throws SocketException {
		requestSocket = new DatagramSocket(port);
	}
	
	@Override
	public void execute() {
         DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
         try {
			requestSocket.receive(packet);
			new SimulatorThread(packet).start();
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

}
