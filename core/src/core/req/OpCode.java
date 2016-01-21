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


}
