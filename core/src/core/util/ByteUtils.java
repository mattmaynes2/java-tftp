
package core.util;

import java.security.InvalidParameterException;

public class ByteUtils {

	private final static int MAX_INT =65536; // maximum int that can be stored in 2 bytes
    public static int indexOf (byte[] array, byte value) {
        return ByteUtils.indexOf(array, 0, value);
    }

    public static int indexOf (byte[] array, int start, byte value) {
        for (int i = start; i < array.length; i++){
            if(array[i] == value){
                return i;
            }
        }
        return -1;
    }
    
    public static int bytesToInt(byte[] bytes){
    	if(bytes.length==2){
    		int high= bytes[1]>=0? bytes[1]:256+bytes[1];
    		int low =bytes[0]>=0? bytes[0]:256+bytes[0];
    		return low |(high>>8);
    	}
    	throw new InvalidParameterException("Byte Array must be 2 bytes long");
    }
    
    public static byte[] intToByteArray(int num){
    	byte[] bytes=new byte[2];
    	num=num%MAX_INT;
    	bytes[0]=(byte)(num & 0xFF);
    	bytes[1]=(byte)((num>>8) & 0xFF);
    	return bytes;
    }

	public static String bytesToHexString(byte[] bytes) {
		StringBuilder s= new StringBuilder();
		for(byte b: bytes) {
			s.append(Integer.toHexString(b));
		}
		return s.toString();
	}

}
