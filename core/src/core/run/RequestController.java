package core.run;

import core.run.Controller;

import core.net.NodeSocket;
import core.net.ReadTransferRunner;
import core.net.WriteTransferRunner;

import core.req.RequestHandler;
import core.req.Request;
import core.req.OpCode;
import core.req.AckMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public abstract class RequestController extends Controller implements RequestHandler {

    public RequestController (NodeSocket socket){
        super(socket);
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
        WriteTransferRunner runner;
        FileInputStream in;

        try {
            in = new FileInputStream(filename);
            runner = new WriteTransferRunner(this.socket, in);

            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void write (String filename){
        ReadTransferRunner runner;
        FileOutputStream out;

        try {
            out = new FileOutputStream(filename);
            runner = new ReadTransferRunner(this.socket, out);

            // Send the initial Ack
            this.socket.send(new AckMessage(0));

            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

    }
}
