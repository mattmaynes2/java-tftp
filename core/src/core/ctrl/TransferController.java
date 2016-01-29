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

/**
 * Transfer Controller
 *
 * Spawns transfers when user enters CLI commands
 */
public abstract class TransferController extends Controller {

    /**
     * Command to initialize a read command
     */
    public static final String READ_COMMAND = "read";

    /**
     * Command to initialize a write request
     */
    public static final String WRITE_COMMAND = "write";

    /**
     * Constructs a transfer controller with a read and write command
     *
     * @param address - Address of endpoint to communicate with
     */
    public TransferController (SocketAddress address) {
        super(address);
        this.interpreter.addCommand(READ_COMMAND);
        this.interpreter.addCommand(WRITE_COMMAND);
    }

    /**
     * Handles the read and write commands from the CLI
     *
     * @param command - Command from the user interface
     */
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

    /**
     * Performs a read transfer of the given filename from an endpoint
     * to this controller
     *
     * @param filename - Name of file to transfer
     */
    public void read (String filename){
        ReadTransfer runner;

        try {
            runner = new ReadTransfer(this.getAddress(), filename);
            runner.addTransferListener(this);

            runner.sendRequest();
            performTransfer(runner);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Performs a write transfer of the given filename from this controller
     * to another endpoint
     *
     * @param filename - Name of file to transfer
     */
    public void write (String filename){
        WriteTransfer runner;

        try {
            runner = new WriteTransfer(this.getAddress(), filename);
            runner.addTransferListener(this);
            runner.sendRequest();
            runner.getAcknowledge();

            performTransfer(runner);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Runs a transfer in a background thread
     *
     * @param transfer - Transfer to run in background thread
     */
    public void performTransfer (Transfer transfer) throws InterruptedException{
        Thread transferThread = new Thread(transfer);
        transferThread.start();
        transferThread.join();
    }
}
