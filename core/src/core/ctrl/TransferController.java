package core.ctrl;

import core.ctrl.Controller;

import core.net.NodeSocket;
import core.net.ReadTransfer;
import core.net.Transfer;
import core.net.TransferListener;
import core.net.WriteTransfer;
import core.req.Message;
import core.cli.Command;

import java.net.SocketAddress;

public abstract class TransferController extends Controller implements TransferListener {


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

        try {
            runner = new ReadTransfer(new NodeSocket(this.getAddress()), filename);

            runner.sendRequest();
            performTransfer(runner);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void write (String filename){
        WriteTransfer runner;

        try {
            runner = new WriteTransfer(new NodeSocket(this.getAddress()), filename);

            runner.sendRequest();
            runner.getAcknowledge();

            performTransfer(runner);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void performTransfer(Transfer transfer) throws InterruptedException{
        Thread transferThread = new Thread(transfer);
        transfer.addTransferListener(this);
        transferThread.start();
        transferThread.join();
    }

    public abstract void handleStart ();

    public abstract void handleMessage (Message msg);

    public abstract void handleComplete ();

}
