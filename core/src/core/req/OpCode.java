package core.req;

public enum OpCode {
    READ    ((byte) 0x01),
    WRITE   ((byte) 0x02),
    DATA    ((byte) 0x03),
    ACK     ((byte) 0x04),
    ERROR   ((byte) 0x05);

    private byte code;

    OpCode (byte code) {
        this.code = code;
    }

    public byte getCode(){
        return this.code;
    }

    public static OpCode convert(byte code) {
    	return OpCode.values()[code - 1];
    }
    
    public static boolean isValid(byte code) {
    	if (code < 1 || code > 5) {
    		return false;
    	}
    	
    	return true;
    }
}
