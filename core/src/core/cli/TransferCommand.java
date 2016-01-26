package core.cli;

public abstract class TransferCommand extends Command {

    private String fileName;

    public TransferCommand(String fileName){
        this.fileName = fileName;
    }

    public String getFileName(){
        return fileName;
    }
}
