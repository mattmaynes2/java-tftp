package core.ctrl;

import java.net.SocketAddress;
import java.net.SocketException;

import core.net.ReadTransfer;
import core.net.RequestHandler;
import core.net.WriteTransfer;
import core.req.AckMessage;
import core.req.Request;


public abstract class RequestController extends Controller implements RequestHandler {

    public RequestController () throws SocketException {
        super();
    }

    public void handleRequest (Request req, SocketAddress address){
        switch(req.getOpCode()){
            case READ:
                this.read(address, req.getFilename());
                break;
            case WRITE:
                this.write(address, req.getFilename());
                break;
        }
    }

    @Override
    public void start(){
        super.start();
    }

    public void read (SocketAddress address, String filename){
        WriteTransfer runner;

        try {
            runner = new WriteTransfer(address, filename);

            runner.addTransferListener(this);
            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void write (SocketAddress address, String filename){
        ReadTransfer runner;

        try {
            runner = new ReadTransfer(address, filename);

            runner.addTransferListener(this);
            // Send the initial Ack
            runner.getSocket().send(new AckMessage((short)0));

            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

    }
}
