package core.ctrl;

import java.io.File;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import core.cli.Command;
import core.net.ReadTransfer;
import core.net.Transfer;
import core.net.TransferListener;
import core.net.WriteTransfer;

/**
 * Transfer Controller
 *
 * Spawns transfers when user enters CLI commands
 */
public abstract class TransferController extends Controller implements TransferListener  {

    /**
     * Command to change the server to connect to
     */
    public static final String SERVER_COMMAND = "server";

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
     * @param commandLineArgs - Arguments entered by the user at startup
     */
    public TransferController (InetSocketAddress address, String[] commandLineArgs) {
        super(address, commandLineArgs);
        this.interpreter.addCommand(READ_COMMAND);
        this.interpreter.addCommand(WRITE_COMMAND);
        this.interpreter.addCommand(SERVER_COMMAND);
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
                this.read(command.getFirstArgument());
                break;
            case WRITE_COMMAND:
                this.write(command.getFirstArgument());
                break;
            case SERVER_COMMAND:
                this.changeServer(command.getFirstArgument());
                break;
        }
    }

    /**
     * Changes the server that the client will connect to
     * @param firstArgument The hostname of the server to connect to, or a numeric IP address
     */
    private void changeServer(String firstArgument) {
        try {
            InetAddress addr = InetAddress.getByName(firstArgument);
            int port = this.getAddress().getPort();
            setAddress(new InetSocketAddress(addr, port));
        } catch (UnknownHostException e) {
            cli.message("Unknown host. Please specify a valid hostname or numeric IP address");
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
        File file, dir;
        String path;


        path = this.appendPrefix(filename);
        dir  = new File(this.getPrefix());
        file = new File(path);
        new File(getPrefix()).mkdirs();

        System.out.println("Requesting to read: " + filename);

        // Before starting the transfer, ensure that the file exists
        // and that there are sufficient permissions to read from it
        if (file.exists() && file.isFile()) {
            System.out.println("File already exists: " + filename +
                    "\nEither remove the file from the working directory, "
                    + "or change the working directory.");
            return;
        }
        else if (dir.exists() && !dir.canWrite()) {
            System.out.println("Insufficient permissions to write file: "
                    + filename + "\nPermission denied");
            return;
        }

        // At this point the transfer is read to begin. The
        // requested file does not already exist and there are
        // sufficient privileges to write
        try {
            runner = new ReadTransfer(this.getAddress(), filename, path);
            runner.addTransferListener(this);

            if (runner.sendRequest()){
                performTransfer(runner);
            }
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
    public void write (String filename) {
        Transfer runner;
        File file;
        String path;

        path = this.appendPrefix(filename);
        file = new File(path);

        System.out.println("Requesting to write: " + filename);
        if (!file.exists()) {
            System.out.println("File not found: " + filename);
            return;
        }
        else if (!file.canRead()) {
            System.out.println("Insufficient permissions to read file: "
                    + filename + "\nPermission denied");
            return;
        }

        // At this point the transfer is read to being.
        // There are no issues with permissions on this end of
        // the transfer
        try {
            runner = new WriteTransfer(this.getAddress(), appendPrefix(filename), filename);
            runner.addTransferListener(this);
            System.out.println("Client Filename: " + appendPrefix(filename));

            if (runner.sendRequest()){
                performTransfer(runner);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Runs a transfer in a background thread
     *
     * @param transfer - Transfer to run in background thread
     *
     * @throws InterruptedException - If the transfer gets killed externally
     */
    public void performTransfer (Transfer transfer) throws InterruptedException {
        Thread transferThread = new Thread(transfer);
        transferThread.start();
        transferThread.join();
    }

}
