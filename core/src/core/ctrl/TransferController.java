package core.run;

import core.run.Controller;

import core.net.NodeSocket;
import core.net.ReadTransfer;
import core.net.WriteTransfer;

import core.cli.Command;

import java.io.FileOutputStream;
import java.io.FileInputStream;

import java.net.SocketAddress;

public abstract class TransferController extends Controller {


    public static final String READ_COMMAND     = "read";
    public static final String WRITE_COMMAND    = "write";

    public TransferController (SocketAddress address){
        super(address);
        this.interpreter.addCommand(READ_COMMAND);
        this.interpreter.addCommand(WRITE_COMMAND);
    }

    @Override
    public void handleCommand (Command command){
        super.handleCommand(command);

        switch (command.getToken()){
            case READ_COMMAND:
                this.read(command.getArgument());
                break;
            case WRITE_COMMAND:
                this.write(command.getArgument());
                break;
        }
    }

    public void read (String filename){
        ReadTransfer runner;
        FileOutputStream out;
        try {
            out = new FileOutputStream(filename);
            runner = new ReadTransfer(new NodeSocket(this.getAddress()), out);

            runner.sendRequest(filename);
            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void write (String filename){
        WriteTransfer runner;
        FileInputStream in;

        try {
            in = new FileInputStream(filename);
            runner = new WriteTransfer(new NodeSocket(this.getAddress()), in);

            runner.sendRequest(filename);

            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }


}
