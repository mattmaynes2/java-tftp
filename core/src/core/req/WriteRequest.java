
package core.req;

import core.req.OpCode;
import core.req.Request;

public class WriteRequest extends Request {

    public WriteRequest () {
        super(OpCode.WRITE);
    }

    public WriteRequest (byte[] data) {
        super(data);
    }
}
