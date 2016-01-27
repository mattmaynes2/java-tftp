package core.run;

import core.run.Controller;
import core.run.ControllerCommand;

import core.net.NodeSocket;
import core.net.ReadTransferRunner;
import core.net.WriteTransferRunner;

import core.cli.Command;

import java.io.FileOutputStream;
import java.io.FileInputStream;

public abstract class TransferController extends Controller {

    public TransferController (NodeSocket socket){
        super(socket);
    }

    @Override
    public void handleCommand (Command command){
        super.handleCommand(command);

        switch (ControllerCommand.createCommand(command.getToken())){
            case READ:
                this.read(command.getArgument());
                break;
            case WRITE:
                this.write(command.getArgument());
                break;
        }
    }

    public void read (String filename){
        ReadTransferRunner runner;
        FileOutputStream out;
        try {
            out = new FileOutputStream(filename);
            runner = new ReadTransferRunner(this.socket, out);

            runner.sendRequest(filename);
            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void write (String filename){
        WriteTransferRunner runner;
        FileInputStream in;

        try {
            in = new FileInputStream(filename);
            runner = new WriteTransferRunner(this.socket, in);

            runner.sendRequest(filename);

            (new Thread(runner)).start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }


}
