
package core.util;

public class ByteUtils {

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

}
