package host;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import core.req.Message;


public class ErrorSimulator {

	
	public static void main(String[] args) {
		int client_port;
		DatagramSocket requestSocket;
		try {
			requestSocket = new DatagramSocket(68);
			while(true){
				DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
				requestSocket.receive(packet);
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
