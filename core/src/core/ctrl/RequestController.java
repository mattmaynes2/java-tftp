package core.ctrl;

import core.ctrl.Controller;

import core.net.ReadTransfer;
import core.net.WriteTransfer;
import core.net.RequestListener;
import core.net.RequestReceiver;
import core.req.Request;
import core.req.OpCode;
import core.req.AckMessage;

import java.net.SocketAddress;
import java.net.SocketException;

public abstract class RequestController extends Controller implements RequestListener {

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
