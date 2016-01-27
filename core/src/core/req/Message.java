package core.req;

import java.io.ByteArrayOutputStream;

public abstract class Message {

    private OpCode opCode;

    public Message(byte[] bytes) throws InvalidMessageException {
        decode(bytes);
    }

    public Message(OpCode opCode) {
        this.opCode = opCode;
    }

    protected void decode(byte[] bytes) throws InvalidMessageException {
        this.opCode = OpCode.convert(bytes[1]);
    }

    public OpCode getOpCode() {
        return this.opCode;
    }

    public byte[] toBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0);
        out.write(getOpCode().getCode());
        return out.toByteArray();
    }

    public String toString() {
        return "OpCode: " + getOpCode().getCode();
    }
}
