
package core.req;

/**
 * Enum containing the valid possibilities of a request mode, and some functionality associated to them.
 *
 */
public enum RequestMode {
    ASCII   ("netascii"),
    BINARY  ("octet");

    private String mode;

    /**
     * Constructor sets the mode
     * @param mode -  The string to set the mode with
     */
    RequestMode (String mode) {
        this.mode = mode;
    }

    /**
     * Get the mode as a String
     * @return The mode
     */
    public String getMode () {
        return this.mode;
    }

    /**
     * Returns the RequestMode based on string input
     * @param mode  the desired mode
     * @return A mode enumeration value
     */
    public static RequestMode convert (String mode){
        switch(mode.toLowerCase()){
            case "netascii":
                return RequestMode.ASCII;
            case "octet":
                return RequestMode.BINARY;
            default:
                throw new IllegalArgumentException("Request mode must be either \"netascii\" or \"octet\"");
        }
    }

    /**
     * Get the mode as a bytelist
     * @return  the mode
     */
    public byte[] getBytes() {
        return this.mode.getBytes();
    }

}
