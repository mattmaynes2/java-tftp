package core.req;

import java.util.Arrays;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class ErrorMessage extends Message {

    private ErrorCode errorCode;

    private String message;

    public ErrorMessage (ErrorCode errorCode, String message) {
        super(OpCode.ERROR);
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorMessage (byte[] bytes) throws InvalidMessageException {
        super(bytes);
    }

    public ErrorCode getErrorCode () {
        return this.errorCode;
    }

    public void setErrorCode (ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage () {
        return this.message;
    }

    public String toString () {
        return super.toString() + " error code: " + this.errorCode +
            " message: " + this.message;
    }

    public void decode (byte[] data) throws InvalidMessageException {
        super.decode(data);

        this.errorCode = ErrorCode.convert(data[3]);
        this.message = new String(Arrays.copyOfRange(data, 4, data.length - 1));

    }

    public byte[] toBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            out.write(super.toBytes());
            out.write(0x00);
            out.write(this.errorCode.getCode());
            out.write(this.message.getBytes());
            out.write(0x00);
        } catch (IOException e){
            e.printStackTrace();
        }
        return out.toByteArray();
    }

}
