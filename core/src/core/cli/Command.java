package core.cli;

import java.util.ArrayList;

/**
 * Defines the layout of a command
 * 
 */
public class Command {

    private String token;
    private ArrayList<String> arg;

    /**
     * Allows a command to be formed without any arguments
     * @param token  the command token
     */
    public Command (String token) {
        this(token, new ArrayList<String>());
    }

    /**
     * Sets the command token and its arguments
     * @param token  the command token
     * @param arg  the command arguments 
     */
    public Command (String token, ArrayList<String> arg){
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
    public ArrayList<String> getArguments () {
        return this.arg;
    }
    
    /**
     * Get the command arguments
     * @throws IndexOutOfBoundsException if there are no arguments
     * @return
     */
    public String getFirstArgument () throws IndexOutOfBoundsException{
        return this.arg.get(0);
    }
    
    

}
