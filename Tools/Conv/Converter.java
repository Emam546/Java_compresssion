package Tools.Conv;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Converter {
    public static byte[] num2Bytes(int num) {
        return num2Bytes(num, 4, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] num2Bytes(int num, int size) {
        return num2Bytes(num, size, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] num2Bytes(int num, int size, ByteOrder order) {
        return ByteBuffer.allocate(size).order(order).putInt(num).array();
    }

    public static byte[] num2Bytes(short num) {
        return num2Bytes(num, 2, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] num2Bytes(short num, int size) {
        return num2Bytes(num, size, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] num2Bytes(short num, int size, ByteOrder order) {
        return ByteBuffer.allocate(size).order(order).putShort(num).array();
    }

    public static byte[] num2Bytes(long num) {
 
        return num2Bytes(num, 8, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] num2Bytes(long num, int size) {
        return num2Bytes(num, size, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] num2Bytes(long num, int size, ByteOrder order) {
        return ByteBuffer.allocate(size).order(order).putLong(num).array();
    }

    public static Byte[] bytes2Bytes(byte[] bytes) {
        Byte[] byteObjects = new Byte[bytes.length];
        int i = 0;
        for (byte b : bytes)
            byteObjects[i++] = b;
        return byteObjects;
    }

    public static byte[] Bytes2bytes(Byte[] bytes) {
        byte[] byteObjects = new byte[bytes.length];
        int i = 0;
        for (Byte b : bytes)
            byteObjects[i++] = b;
        return byteObjects;
    }

    public static int byte2int(byte num){
        if(num>=0)
            return (int)num;
        return (int)num+256;
    }

}
