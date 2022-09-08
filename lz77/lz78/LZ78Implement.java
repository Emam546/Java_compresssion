package lz77.lz78;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Tools.Size;
import Tools.Tools_funct;
import Tools.Conv.Converter;
import Tools.Conv.Wrappers;
import Tools.BytesArr;

public class LZ78Implement {
    static Byte[] num2Bytes(Integer num, int presentByte) {
        switch (presentByte) {
            case 1:
                return new Byte[] { num.byteValue() };
            case 2:
            case 3:
                return Converter.bytes2Bytes(Converter.num2Bytes(num.shortValue(), presentByte));
            case 4:
            case 5:
            case 6:
            case 7:
                return Converter.bytes2Bytes(Converter.num2Bytes(num.intValue(), presentByte));
            default:
                return Converter.bytes2Bytes(Converter.num2Bytes(num.longValue(), presentByte));

        }
    }

    public static Iterable<Byte> transferData(Iterable<Byte> data, int presentByte) {
        return Wrappers.wrapIterArr(() -> new Iterator<Byte[]>() {
            Iterator<Byte> iter = data.iterator();
            BytesArr currentByte = new BytesArr();
            Integer currentIndex = 0;
            Boolean init = false;
            HashMap<BytesArr, Integer> storedDict = new HashMap<BytesArr, Integer>() {
                {
                    this.put(new BytesArr(), 0);
                }
            };

            @Override
            public boolean hasNext() {
                return iter.hasNext() || currentByte != null;
            }

            Integer res = null;

            @Override
            public Byte[] next() {
                if (!init) {
                    init = true;
                    return new Byte[]{(byte)presentByte};
                }
                while (iter.hasNext()) {
                    Byte b = iter.next();
                    if ((res = storedDict.get(currentByte)) != null) {
                        currentIndex = res;
                        currentByte.add(b);
                        continue;
                    }
                    storedDict.put(currentByte, storedDict.size());
                    Byte[] byteArr = Tools_funct.concatArr(num2Bytes(currentIndex, presentByte),new Byte[]{currentByte.get(currentByte.size()-1)});

                    currentByte = new BytesArr(b);
                    currentIndex = 0;
                    return byteArr;
                }
                
                if (storedDict.containsKey(currentByte)) {
                    currentIndex = storedDict.get(currentByte);
                    Byte[] res=num2Bytes(currentIndex,presentByte);
                    currentByte=null;
                    return res;
                } else if (currentByte.size() == 1){
                    Byte[] res=Tools_funct.concatArr(num2Bytes(0, presentByte), currentByte.toArray());
                    currentByte=null;
                    return res;
                }
                else {
                    iter = Wrappers.wrapArr(currentByte.toArray()).iterator();
                    currentByte = new BytesArr();
                    currentIndex = 0;
                    return next();
                }

            }
        });
    }

    public static int Bytes2Num(Byte[] arr) {
        ByteBuffer b = Size.wrap(Wrappers.wrapArr(arr), arr.length);
        
        switch (arr.length) {
            case 1:
                return Converter.byte2int(b.get());
            case 2:
            case 3:
                return (int)b.getShort();
            case 4:
            case 5:
            case 6:
            case 7:
                return (int)b.getInt();

            default:
                return (int)b.getLong();
        }
    }

    public static Iterable<Byte> retransferData(Iterable<Byte> data) {
        return Wrappers.wrapIterArr(() -> new Iterator<Byte[]>() {
            ArrayList<BytesArr> storedDict = new ArrayList<BytesArr>() {
                {
                    this.add(new BytesArr());
                }
            };
            Iterator<Byte> iter = data.iterator();
            int presentByte;
            {
                if (iter.hasNext())
                    presentByte = Converter.byte2int(iter.next());
                System.out.println(presentByte);
            }

            @Override
            public boolean hasNext() {
                return iter.hasNext() && presentByte != 0;
            }

            @Override
            public Byte[] next() {
                BytesArr arr =new BytesArr();
                for (byte i = 0; i < presentByte; i++)
                    if (iter.hasNext())
                        arr.add(iter.next());

                int index = Bytes2Num(arr.toArray());
                BytesArr res = storedDict.get(index).clone();
                if (iter.hasNext()) {
                    byte newByte = iter.next();
                    res.add(newByte);
                    storedDict.add(res);
                }
                return res.toArray();
            }

        });
    }

}
