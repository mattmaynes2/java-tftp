package core.cli;

public class Command {

    private String token;
    private String arg;

    public Command (String token) {
        this(token, "");
    }

    public Command (String token, String arg){
        this.token = token;
        this.arg = arg;
    }

    public String getToken () {
        return this.token;
    }

    public String getArgument () {
        return this.arg;
    }

}
