package core.run;

import core.run.ControllerCommand;

import core.net.NodeSocket;

import core.cli.CLI;
import core.cli.CommandHandler;
import core.cli.CommandInterpreter;
import core.cli.Command;

import java.net.SocketException;

public abstract class Controller implements CommandHandler {

    protected NodeSocket socket;
    protected CommandInterpreter interpreter;

    private CLI cli;

    public Controller(NodeSocket socket){
    	this.socket = socket;
    	this.interpreter = new CommandInterpreter();

		this.interpreter.addCommand("shutdown");
		this.interpreter.addCommand("help");
    }
    
    public NodeSocket getSocket(){
    	return socket;
    }
    
    public void setSocket(NodeSocket socket){
    	this.socket = socket;
    }
    
    public void start () {
        this.cli = new CLI(this.interpreter, System.in, System.out);
        this.cli.addCommandHandler(this);
        this.cli.start();
    }

    public void stop () {
        this.socket.close();
        this.cli.stop();
    }

    public abstract void usage();

    public void handleCommand (Command command){
        switch (ControllerCommand.createCommand(command.getToken())){
            case SHUTDOWN:
                this.stop();
                break;
            case HELP:
                this.usage();
                break;
            default:
            	break;
        }
    }

}
