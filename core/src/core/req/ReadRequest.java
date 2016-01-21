
package core.req;

import core.req.OpCode;
import core.req.Request;
import core.req.InvalidMessageException;

public class ReadRequest extends Request {

    public ReadRequest () {
        super(OpCode.READ);
    }

    public ReadRequest (byte[] data) throws InvalidMessageException {
        super(data);
    }
}
