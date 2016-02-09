import core.req.Message;
import core.req.ErrorMessage;

import core.log.Logger;
import core.ctrl.TransferController;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import java.util.logging.Level;

public class Client extends TransferController {

    private static final int SERVER_PORT = 69;
    private static final int ERROR_SIMULATOR_PORT = 68;
    private final static String TEST_MODE_FLAG = "t";
    private final static String QUIET_MODE_FLAG = "q";

    private static Boolean testMode = false;
    private static Boolean quietMode = false;

    public Client (SocketAddress address){
        super(address);
    }

    @Override
    public void usage() {
        System.out.println("TFTP Client");
        System.out.println("  Commands:");
        System.out.println("    help           Prints this message");
        System.out.println("    read  <file>   Reads a file from a tftp server to the current working directory");
        System.out.println("    write <file>   Writes a file from the current working directory to a tftp server");
        System.out.println("    shutdown       Exits the client");
    }

    @Override
    public void handleStart() {
        this.cli.message("Transfer started");
    }

    @Override
    public void handleMessage(Message msg) {
        Logger.log(Level.FINE, "Received transfer message: " + msg.toString());
    }

    @Override
    public void handleSendMessage(Message msg) {
        Logger.log(Level.FINE, "Sending message: " + msg.toString());
    }

    public void handleErrorMessage (ErrorMessage err) {
        Logger.log(Level.SEVERE, "Received error message: " + err.toString());
    }

    @Override
    public void handleComplete() {
        this.cli.message("Transfer complete");
    }

    public static void main(String[] args){
        Client client;
        setCommandLineOptions(args);

        int port = SERVER_PORT;
        if (testMode){
            port = ERROR_SIMULATOR_PORT;
        }

        Level logLevel = Level.FINEST;
        if (quietMode){
            logLevel = Level.SEVERE;
        }
        Logger.init(logLevel);

        try {
            InetSocketAddress address =
                new InetSocketAddress(InetAddress.getLocalHost(), port);

            client = new Client(address);
            client.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static void setCommandLineOptions(String[] args){
        for (int i=0; i < args.length; i++){
            if (args[i].startsWith("-") && args[i].length() > 1){
                setOption(args[i].substring(1));
            }
        }
    }

    private static void setOption(String option){
        if (option.equals(TEST_MODE_FLAG)){
            testMode = true;
        }else if(option.equals(QUIET_MODE_FLAG)){
            quietMode = true;
        }
    }
}
