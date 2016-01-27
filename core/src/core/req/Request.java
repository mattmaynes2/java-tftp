
package core.req;

import core.req.Message;
import core.req.OpCode;
import core.req.RequestMode;

import core.util.ByteUtils;

import java.util.Arrays;

import java.io.ByteArrayOutputStream;

public abstract class Request extends Message {

    private RequestMode mode;
    private String filename;

    public Request (OpCode code, String filename) {
        super(code);
        this.filename = filename;
    }

    public Request (byte[] data) throws InvalidMessageException {
        super(data);
    }

    public void setMode (RequestMode mode){
        this.mode = mode;
    }

    public RequestMode getMode () {
        return this.mode;
    }

    public void setFilename (String filename){
        this.filename = filename;
    }

    public String getFilename () {
        return this.filename;
    }

    protected void decode (byte[] data) throws InvalidMessageException {
        int fileIndex, modeIndex;

        super.decode(data);

        fileIndex = ByteUtils.indexOf(data, 2, (byte) 0x00);
        this.filename = new String(Arrays.copyOfRange(data, 2, fileIndex));

        modeIndex = ByteUtils.indexOf(data, fileIndex + 1, (byte) 0x00);
        this.mode = RequestMode.convert(new String(
            Arrays.copyOfRange(data, fileIndex, modeIndex)
            ));

    }

    public byte[] toBytes () {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            buffer.write(super.toBytes());
            buffer.write(this.filename.getBytes());
            buffer.write(0x00);
            buffer.write(this.mode.getBytes());
            buffer.write(0x00);
        } catch (Exception e){
            // This only happens if we run out of memory
            e.printStackTrace();
            System.exit(1);
        }

        return buffer.toByteArray();
    }

    public String toString() {
        return super.toString() +
            "\nMode: " + this.mode +
            "\nFile: " + this.filename;
    }

}
