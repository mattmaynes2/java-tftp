package core.cli;

public class ReadCommand extends TransferCommand {

    public ReadCommand(String fileName) {
        super(fileName);
    }

    public void execute(CommandHandler handler){
        handler.handleReadCommand(this);
    }
}
