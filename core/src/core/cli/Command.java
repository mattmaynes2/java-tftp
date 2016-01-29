package core.cli;

/**
 * Defines the layout of a command
 * 
 */
public class Command {

    private String token;
    private String arg;

    /**
     * Allows a command to be formed without any arguments
     * @param token  the command token
     */
    public Command (String token) {
        this(token, "");
    }

    /**
     * Sets the command token and its arguments
     * @param token  the command token
     * @param arg  the command arguments 
     */
    public Command (String token, String arg){
        this.token = token;
        this.arg = arg;
    }

    /**
     * Get the command token
     * @return  the token
     */
    public String getToken () {
        return this.token;
    }

    /**
     * Get the command arguments
     * @return
     */
    public String getArgument () {
        return this.arg;
    }

}
