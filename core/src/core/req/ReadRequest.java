
package core.req;

import core.req.OpCode;
import core.req.Request;

public class ReadRequest extends Request {

    public ReadRequest () {
        super(OpCode.READ);
    }

    public ReadRequest (byte[] data) {
        super(data);
    }
}
