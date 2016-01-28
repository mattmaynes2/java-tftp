package core.ctrl;

import core.ctrl.Controller;

import core.net.NodeSocket;
import core.net.ReadTransfer;
import core.net.WriteTransfer;
import core.net.RequestHandler;
import core.net.RequestListener;
import core.req.Request;
import core.req.OpCode;
import core.req.AckMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.SocketAddress;
import java.net.SocketException;

public abstract class RequestController extends Controller implements RequestHandler {
    
    public RequestController () throws SocketException {
        super();
    }

    public void handleRequest (Request req, SocketAddress address){
        switch(req.getOpCode()){
            case READ:
                this.read(req.getFilename(), address);
                break;
            case WRITE:
                this.write(req.getFilename(), address);
                break;
        }
    }

    @Override
    public void start(){
    	super.start();
    }
    
    public void read (String filename, SocketAddress address){
        WriteTransfer runner;
        FileInputStream in;

        try {
            in = new FileInputStream(filename);
            NodeSocket socket = new NodeSocket(address);
            runner = new WriteTransfer(socket, in);

            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void write (String filename, SocketAddress address){
        ReadTransfer runner;
        FileOutputStream out;

        try {
            out = new FileOutputStream(filename);
            NodeSocket socket = new NodeSocket(address);
            runner = new ReadTransfer(socket, out);
 
            // Send the initial Ack           
            socket.send(new AckMessage((short)0));

            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

    }
}
