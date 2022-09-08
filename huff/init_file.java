package huff;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Tools.Tools_funct;
import Tools.Conv.Converter;
import Tools.Conv.Wrappers;
import Tools.Tools_funct.Pair;

public class init_file {
    public static Iterable<Byte> encodeInitCompressData(long size, String file, Map<Byte, String> codec) {
        return Wrappers.wrapIterArr(() -> {
            return new Iterator<Byte[]>() {
                boolean init = true;
                final Iterator<Byte> iter = encodeCodec(codec).iterator();

                @Override
                public boolean hasNext() {
                    return init || iter.hasNext();
                }

                @Override
                public Byte[] next() {
                    if (init) {
                        Byte[] encodeByte = Converter.bytes2Bytes(Tools_funct.encodeFile(size, file));
                        init = false;
                        return encodeByte;
                    }
                    return new Byte[] { iter.next() };
                }

            };
        });
    }

    public static Iterable<Byte> encodeInitCompressData(DecodeResult res) {
        return encodeInitCompressData(res.size, res.filePath, res.codec);
    }

    public static class DecodeResult {
        public final Long size;
        public final String filePath;
        public final Map<Byte, String> codec;

        public DecodeResult(Long size, String filePath, Map<Byte, String> codec) {
            this.size = size;
            this.filePath = filePath;
            this.codec = codec;
        }

    }

    public static Iterable<Byte> encodeCodec(Map<Byte, String> codec) {
        return Wrappers.wrapIterArr(() -> {
            return new Iterator<Byte[]>() {
                boolean init = true;
                final Iterator<Byte> iter = codec.keySet().iterator();

                @Override
                public boolean hasNext() {
                    return init || iter.hasNext();
                }

                @Override
                public Byte[] next() {
                    if (init) {
                        init = false;
                        return new Byte[] { (byte) codec.size() };
                    }
                    Byte b = iter.next();

                    Byte[] binary = Converter.bytes2Bytes(codec.get(b).getBytes());
                    Byte len = (byte) binary.length;
                    return Tools_funct.concatArr(new Byte[] { b, len }, binary);
                }

            };
        });
    }

    public static Map<Byte, String> decodeCodec(Iterable<Byte> data) {
        Map<Byte, String> codec = new HashMap<>();
        Iterator<Byte> iter = data.iterator();
        if (!iter.hasNext())
            return codec;
        int sizeCodecBytes = Converter.byte2int(iter.next());
        for (int i = 0; i < sizeCodecBytes; i++) {
            if (!iter.hasNext())
                return codec;
            Byte keyNum = iter.next();
            if (!iter.hasNext())
                return codec;
            int valueNumBytes = Converter.byte2int(iter.next());
            byte[] codeKey = new byte[valueNumBytes];
            for (int j = 0; j < valueNumBytes; j++) {
                if (iter.hasNext())
                    codeKey[j] = iter.next();
            }

            String code = new String(codeKey, Tools_funct.ENCODING_d);
            codec.put(keyNum, code);
        }
        return codec;
    }

    public static DecodeResult decodeInitCompressData(Iterable<Byte> data) {
        Iterator<Byte> iter = data.iterator();
        Iterable<Byte> iterD = Wrappers.wrapIter(iter);
        Pair<Long, String> res = Tools_funct.decodeFile(iterD);
        Long size = res.key;
        String filePath = res.value;
        Map<Byte, String> codec = decodeCodec(iterD);

        return new DecodeResult(size, filePath, codec);
    }

}
