package core.net;

public enum NodeCommand {
    READ        ("read"),
    WRITE       ("write"),
    SHUTDOWN    ("shutdown"),
    HELP        ("help");

    private String command;

    NodeCommand (String command){
        this.command = command;
    }

    public String getCommand (){
        return this.command;
    }

    public static NodeCommand createCommand(String command){
        for(NodeCommand cmd : NodeCommand.values()){
            if(cmd.getCommand().equals(command)){
                return cmd;
            }
        }
        return null;
    }
}
