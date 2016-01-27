import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import core.cli.CommandInterpreter;
import core.net.NodeSocket;
import core.run.ControllerCommand;
import core.run.TransferController;

public class Client extends TransferController{

	private static final int SERVER_PORT = 69;
	
	public static void main(String[] args){
		Client client;
		try {
			client = new Client();
			InetAddress address = InetAddress.getLocalHost();
			InetSocketAddress socketAddress = new InetSocketAddress(address, SERVER_PORT);
			client.getSocket().setAddress(socketAddress);
			client.start();
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public Client() throws SocketException {
		super(new NodeSocket());
	}

	@Override
	public void usage() {
		
	}

	
}
