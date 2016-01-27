
package core.req;

import core.req.OpCode;
import core.req.Request;
import core.req.InvalidMessageException;

public class ReadRequest extends Request {

    public ReadRequest (String filename) {
        super(OpCode.READ, filename);
    }

    public ReadRequest (byte[] data) throws InvalidMessageException {
        super(data);
    }
}
