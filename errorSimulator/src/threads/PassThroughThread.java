package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import core.log.Logger;
import core.req.InvalidMessageException;
import core.req.Message;
import core.req.MessageFactory;

public class PassThroughThread extends SimulatorThread{

	public PassThroughThread(DatagramPacket packet) throws SocketException, UnknownHostException {
		super(packet);
	}

	@Override
	void handleMessage(Message msg) throws IOException, InvalidMessageException {
		while(!MessageFactory.isLastMessage(msg)) {
			msg=receivePacket();
			Logger.log(Level.INFO,"Message is "+msg);
			sendPacket(msg);
		}

		//Receives the last packet
		msg=receivePacket();
		Logger.log(Level.INFO,"Message is "+msg);
		sendPacket(msg);
		
	}

}
