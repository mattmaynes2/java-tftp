
package core.req;

import core.req.OpCode;
import core.req.Request;
import core.req.InvalidMessageException;

public class WriteRequest extends Request {

    public WriteRequest () {
        super(OpCode.WRITE);
    }

    public WriteRequest (byte[] data) throws InvalidMessageException {
        super(data);
    }
}
