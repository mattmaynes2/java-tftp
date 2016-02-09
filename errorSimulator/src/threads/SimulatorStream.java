package threads;

import java.io.IOException;
import java.net.DatagramPacket;

import core.req.InvalidMessageException;
import core.req.Message;

public interface SimulatorStream {

	public DatagramPacket receive() throws IOException;
	public void send(DatagramPacket packet) throws IOException;
	public int  getNumberPacketsOfPackets();
}
