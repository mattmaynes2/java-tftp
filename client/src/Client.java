import core.req.Message;
import core.req.ErrorMessage;

import core.ctrl.Controller;
import core.ctrl.TransferController;

import core.util.ByteUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import java.util.logging.Level;

public class Client extends TransferController {

    private static final int SERVER_PORT = 69;
    private static final int ERROR_SIMULATOR_PORT = 68;
    private final static String TEST_MODE_FLAG = "t";

    public Client(InetSocketAddress address, String[] commandLineArgs){
        super(address, commandLineArgs);
        if (this.commandLineOptions.getOrDefault(TEST_MODE_FLAG, false)){
            try {
                this.address = new InetSocketAddress(InetAddress.getLocalHost(), ERROR_SIMULATOR_PORT);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void usage() {
        System.out.println("TFTP Client");
        System.out.println("  Commands:");
        System.out.println("    help              Prints this message");
        System.out.println("    cd                Change the working directory (source and destination directory of files)");
        System.out.println("    read   <file>     Reads a file from a tftp server to the current working directory");
        System.out.println("    write  <file>     Writes a file from the current working directory to a tftp server");
        System.out.println("    server <address>  Sets the hostname or IP address of the server to transfer with");
        System.out.println("    shutdown          Exits the client");
    }

    @Override
    public void handleStart() {
        this.cli.message("Transfer started");
    }

    @Override
    public void handleMessage(Message msg) {
        LOGGER.log(Level.FINE, "Received bytes: " + ByteUtils.bytesToHexString(msg.toBytes()));
        LOGGER.log(Level.FINE, "Received message: " + msg.toString());
    }

    @Override
    public void handleSendMessage(Message msg) {
        LOGGER.log(Level.FINE, "Sending bytes: " + ByteUtils.bytesToHexString(msg.toBytes()));
        LOGGER.log(Level.FINE, "Sending message: " + msg.toString());
    }

    public void handleErrorMessage (ErrorMessage err) {
        LOGGER.log(Level.FINE, "Error bytes: " + ByteUtils.bytesToHexString(err.toBytes()));
        LOGGER.log(Level.SEVERE, "Error message: " + err.toString());
        this.cli.message("Finished transfer with errors");
    }

    @Override
    public void handleComplete() {
        this.cli.message("Transfer complete");
    }

    public static void main(String[] args){
        Controller client;

        try {
            InetSocketAddress address =
                new InetSocketAddress(InetAddress.getLocalHost(), SERVER_PORT);

            client = new Client(address, args);
            client.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleException(Exception e) {
        LOGGER.log(Level.SEVERE,e.getMessage());
        this.cli.message("Finished transfer with errors");
    }

    @Override
    public void handleInfo(String info) {
        LOGGER.log(Level.INFO,info);

    }
}
