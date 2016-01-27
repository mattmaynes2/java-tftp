package core.ctrl;

import core.ctrl.Controller;

import core.net.NodeSocket;
import core.net.ReadTransfer;
import core.net.WriteTransfer;
import core.net.RequestHandler;

import core.req.Request;
import core.req.OpCode;
import core.req.AckMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.net.SocketException;

public abstract class RequestController extends Controller implements RequestHandler {

    private NodeSocket socket;

    public RequestController (int port) throws SocketException {
        super();
        this.socket = new NodeSocket(port);
    }

    public void handleRequest (Request req){
        switch(req.getOpCode()){
            case READ:
                this.read(req.getFilename());
                break;
            case WRITE:
                this.write(req.getFilename());
                break;
        }
    }

    public void read (String filename){
        WriteTransfer runner;
        FileInputStream in;

        try {
            in = new FileInputStream(filename);
            runner = new WriteTransfer(this.socket, in);

            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void write (String filename){
        ReadTransfer runner;
        FileOutputStream out;

        try {
            out = new FileOutputStream(filename);
            runner = new ReadTransfer(this.socket, out);

            // Send the initial Ack
            this.socket.send(new AckMessage((short)0));

            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

    }
}
