package stream;

import java.io.IOException;
import java.net.DatagramPacket;

import core.req.InvalidMessageException;

public interface SimulatorStream {

	public DatagramPacket receive() throws IOException;
	public void send(DatagramPacket packet) throws IOException, InvalidMessageException;
	public int  getNumberPacketsOfPackets();
}
