import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import core.ctrl.Controller;
import core.req.Message;


public class ErrorSimulator extends Controller {

    public void handleComplete () {}
    public void handleMessage(Message msg){}
    public void handleStart () {}

    public static void main(String[] args) {
        DatagramSocket requestSocket;

        try {
            requestSocket = new DatagramSocket(68);
            while(true){
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                requestSocket.receive(packet);
                new SimulatorThread(packet).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void usage() {
        System.out.println("Usage:\n\tShutdown\n\tHelp");
    }
}
