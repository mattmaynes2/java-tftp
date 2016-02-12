
package core.req;

import core.req.OpCode;
import core.req.Request;
import core.req.InvalidMessageException;

/**
 * Defines the functionality for a read request
 *
 */
public class WriteRequest extends Request {

	/**
	 * Specifies a file to write as a String
     *
	 * @param filename  the file to write
	 */
    public WriteRequest (String filename) {
        super(OpCode.WRITE, filename);
    }

    /**
     * Specifiec a file to write as a byte list
     *
     * @param data  the encoded filename
     *
     * @throws InvalidMessageException - If the given message does not form a valid request
     */
    public WriteRequest (byte[] data) throws InvalidMessageException {
        super(data);
    }
}
