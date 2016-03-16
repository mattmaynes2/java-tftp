package core.req;

public enum ErrorCode {
	FILE_NOT_FOUND		((byte) 0x01),
	ILLEGAL_OP			((byte) 0x04),
	UNKNOWN_TID		((byte) 0x05);

    private byte code;

    ErrorCode (byte code) {
        this.code = code;
    }

    public byte getCode () {
        return this.code;
    }

    public static ErrorCode convert (byte code) {
        switch(code){
        	case 0x01:
        		return FILE_NOT_FOUND;
            case 0x05:
                return UNKNOWN_TID;
            default:
                return ILLEGAL_OP;
        }
    }

}
