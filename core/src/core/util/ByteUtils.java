
package core.util;

/**
 * Contains static methods to help convert bytes to other formats
 */
public class ByteUtils {
	
	/**
	 * indexOf
	 * Find the index of a value in an array
	 * @param array The array to search through
	 * @param value The value to search for
	 * @return index of the value if found, returns -1 if the value is not found
	 */
    public static int indexOf (byte[] array, byte value) {
        return ByteUtils.indexOf(array, 0, value);
    }

    /**
     * indexOf
     * Find the index of a value in a byte array
     * @param array The array to search through
     * @param start The index to start searching from
     * @param value The value to search for
     * @return The index of the value if found, returns -1 if the value is not found
     */
    public static int indexOf (byte[] array, int start, byte value) {
        for (int i = start; i < array.length; i++){
            if(array[i] == value){
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Converts an array of byte to a hex-formatted string representation
     * 
     * @param bytes The array of bytes to convert
     * @return A hex-formatted string
     */
	public static String bytesToHexString(byte[] bytes) {
		StringBuilder s= new StringBuilder();
		for(byte b: bytes) {
			s.append(Integer.toHexString(b)+", ");
		}
		//remove trailing comma and space
		return "["+s.substring(0, s.length()-2)+"]";
	}

}
