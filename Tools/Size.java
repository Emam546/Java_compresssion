package Tools;
import java.nio.ByteBuffer;
import java.util.Iterator;

public class Size {
    public static ByteBuffer wrap(Iterable<Byte> data,int bytesLength) {
        byte[] size = new byte[bytesLength];
        Iterator<Byte> iter=data.iterator();
        for (int i = 0; i < bytesLength; i++)
            if(iter.hasNext())
                size[i]=iter.next();
        
        return ByteBuffer.wrap(size); // big-endian by default
    }

    public static long getSizeLong(Iterable<Byte> data) {
        return wrap(data, 8).getLong();
    }

    public static short getSizeShort(Iterable<Byte> data) {
        return wrap(data, 2).getShort();
    }

    public static int getSizeInt(Iterable<Byte> data) {
        return wrap(data, 4).getInt();
    }
}
