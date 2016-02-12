
package core.req;

import core.req.OpCode;
import core.req.Request;
import core.req.InvalidMessageException;

/**
 * Defines the functionality for a read request
 *
 */
public class ReadRequest extends Request {

	/**
	 * Specifies a file to read as a String
	 * @param filename  the file to read
	 */
    public ReadRequest (String filename) {
        super(OpCode.READ, filename);
    }

    /**
     * Specifiec a file to read as a byte list
     * @param data  the encoded filename
     * @throws InvalidMessageException - If the given message does not form a valid request
     */
    public ReadRequest (byte[] data) throws InvalidMessageException {
        super(data);
    }
}
