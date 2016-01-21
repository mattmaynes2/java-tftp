
package core.req;

public enum RequestMode {
    ASCII   ("netascii"),
    BINARY  ("octet");

    private String mode;

    RequestMode (String mode) {
        this.mode = mode;
    }

    public String getMode () {
        return this.mode;
    }

    public static RequestMode convert (String mode){
        switch(mode.toLowerCase()){
            case "netascii":
                return RequestMode.ASCII;
            default:
                return RequestMode.BINARY;
        }
    }

	public byte[] getBytes() {
		return this.mode.getBytes();
	}

}
