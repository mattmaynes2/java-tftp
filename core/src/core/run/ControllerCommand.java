package core.run;

public enum ControllerCommand {
    READ        ("read"),
    WRITE       ("write"),
    SHUTDOWN    ("shutdown"),
    HELP        ("help");

    private String command;

    ControllerCommand (String command){
        this.command = command;
    }

    public String getCommand (){
        return this.command;
    }

    public static ControllerCommand createCommand(String command){
        for(ControllerCommand cmd : ControllerCommand.values()){
            if(cmd.getCommand().equals(command)){
                return cmd;
            }
        }
        return null;
    }
}
