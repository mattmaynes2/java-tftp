package core.cli;

public class WriteCommand extends TransferCommand {

    public WriteCommand(String fileName) {
        super(fileName);
    }

    public void execute(CommandHandler handler){
        handler.handleWriteCommand(this);
    }

}
