package core.cli;

public abstract class TransferCommand extends Command {

    private String filename;

    public TransferCommand(String filename){
        this.filename = filename;
    }

    public String getFilename(){
        return filename;
    }
}
