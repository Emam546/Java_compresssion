package Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import Tools.Conv.*;

public class Tools_funct {
    public static final int MAXIMUMPATHNUM = (int) Math.pow(2, (8 * 2));
    public static final String ENCODING = "utf-8";
    public static final Charset ENCODING_d = StandardCharsets.UTF_8;
    public static final int MAX_SIZE = (int) Math.pow(2, (8 * 8));

    public static class GetDataBytes extends FileInputStream {
        protected long CURRENT_BYTE = 0;

        public long getCURRENT_BYTE() {
            return CURRENT_BYTE;
        }

        progressBar bar = new progressBar();
        final long size;

        public GetDataBytes(File file) throws FileNotFoundException {
            super(file);
            this.size = file.length();
            this.bar.total = this.size;
        }

        public GetDataBytes(String name) throws FileNotFoundException {
            super(name);
            this.size = new File(name).length();
            this.bar.total = this.size;
        }

        @Override
        public int read() throws IOException {
            CURRENT_BYTE++;
            bar.tick(CURRENT_BYTE);
            return super.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            CURRENT_BYTE += b.length;
            bar.tick(CURRENT_BYTE);
            return super.read(b);
        }

        @Override
        public byte[] readNBytes(int len) throws IOException {
            CURRENT_BYTE += len;
            bar.tick(CURRENT_BYTE);
            return super.readNBytes(len);
        }

        @Override
        public int readNBytes(byte[] b, int off, int len) throws IOException {
            CURRENT_BYTE += (len - off);
            bar.tick(CURRENT_BYTE);
            return super.readNBytes(b, off, len);
        }

        public long getSize() {
            return size;
        }

    }

    public static byte[] encodeFile(long size, String file) {
        if (MAX_SIZE/2 < size)
            throw new RuntimeException("THE FILE THAT YOU WOULD COMPRESS REACHES THE MAXIMUM SIZE");

        byte[] express_size = Converter.num2Bytes(size,8);
        return concatArr(express_size, encodePath(file));
    }

    public static Pair<Long, String> decodeFile(Iterable<Byte> data) {
        Iterable<Byte> iterD=Wrappers.wrapIter(data.iterator());
        long size = Size.getSizeLong(iterD);
        return new Pair<Long, String>(size, decodePath(iterD));
    }

    public static <T> T[] concatArr(T[]... arrs) {
        int totalLen = 0;
        for (T[] ts : arrs) {
            totalLen += ts.length;
        }

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(arrs[0].getClass().getComponentType(), totalLen);
        int curr = 0;
        for (T[] ts : arrs) {
            System.arraycopy(ts, 0, c, curr, ts.length);
            curr += ts.length;
        }

        return c;
    }

    public static byte[] concatArr(byte[]... arrs) {
        int totalLen = 0;
        for (byte[] ts : arrs) {
            totalLen += ts.length;
        }

        byte[] c = (byte[]) Array.newInstance(arrs[0].getClass().getComponentType(), totalLen);
        int curr = 0;
        for (byte[] ts : arrs) {
            System.arraycopy(ts, 0, c, curr, ts.length);
            curr += ts.length;
        }
        return c;
    }

    public static byte[] encodePath(String dir) {
        byte[] file_path_bytes = dir.getBytes(ENCODING_d);
        int numbytes = file_path_bytes.length;
        if (numbytes > MAXIMUMPATHNUM / 2)
            throw new RuntimeException("THE FILE REACHED MAXIMUM PATHFILE");
        byte[] arrBytes = Converter.num2Bytes((short) numbytes);
        return concatArr(arrBytes, file_path_bytes);
    }

    public static String decodePath(Iterable<Byte> data) {
        Iterator<Byte> iter = data.iterator();
        short fileLength = Size.getSizeShort(Wrappers.wrapIter(iter));
        byte[] filePath = new byte[fileLength];
        for (int i = 0; i < filePath.length; i++)
            if (iter.hasNext())
                filePath[i] = iter.next();
        return new String(filePath, ENCODING_d);
    }

    public static Iterable<Byte[]> encodeAllPaths(Iterable<String> files) {
        return new Iterable<Byte[]>() {
            @Override
            public Iterator<Byte[]> iterator() {
                return new Iterator<Byte[]>() {
                    final Iterator<String> iter = files.iterator();
                    boolean init = true;

                    public boolean hasNext() {
                        return init||iter.hasNext();
                    }

                    @Override
                    public Byte[] next() {
                        if (init) {
                            long n = 0;
                            for (String s : files)
                                n++;
                            init = false;
                            return Converter.bytes2Bytes(Converter.num2Bytes(n));
                        }
                        byte[] bytes = encodePath(iter.next());
                        return Converter.bytes2Bytes(bytes);

                    }
                };
            }

        };
    }

    public static Iterable<String> decodeAllPaths(Iterable<Byte> data) {
        return () -> {
            return new Iterator<String>() {
                Iterator<Byte> iter = data.iterator();
                Iterable<Byte> iterDG = Wrappers.wrapIter(iter);
                long fileLength = Size.getSizeLong(iterDG);
                long count=0;
                @Override
                public boolean hasNext() {
                    
                    return count<fileLength;
                }
                @Override
                public String next() {
                    System.out.println(fileLength);
                    count++;
                    return decodePath(iterDG);
                }

            };

        };
    }

    public static class Pair<T1, T2> {
        public final T1 key;
        public final T2 value;

        public static <P, Q> Pair<P, Q> makePair(P p, Q q) {
            return new Pair<P, Q>(p, q);
        }

        public Pair(T1 first, T2 second) {
            this.key = first;
            this.value = second;
        }

    }

    public static Iterable<String> getAllDirs(String dirFile, boolean absolutePath) throws IOException {
        return new Iterable<String>() {
            String parentName = new File(dirFile).getCanonicalFile().getName();
            Iterable<File> data = Wrappers.wrapArr(new File(dirFile).listFiles(File::isDirectory));

            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    final Iterator<File> iter = data.iterator();
                    Iterator<String> smallIter;

                    @Override
                    public boolean hasNext() {
                        return (smallIter != null && smallIter.hasNext()) || iter.hasNext();
                    }

                    @Override
                    public String next() {
                        if (smallIter != null) {
                            if (smallIter.hasNext())
                                return returnName(new File(smallIter.next()));
                            else
                                smallIter = null;
                        }

                        File child = iter.next();
                        try {
                            smallIter = getAllDirs(child.getCanonicalPath(), absolutePath).iterator();
                        } catch (IOException e) {
                            throw new RuntimeException(new IOException());
                        }
                        return returnName(new File(child.getName()));

                    }

                    String returnName(File child) {
                        if (absolutePath)
                            try {
                                return child.getCanonicalPath();
                            } catch (IOException e) {
                                throw new RuntimeException(new IOException());
                            }
                        else
                            return new File(parentName, child.getPath()).getPath();
                    }
                };
            }
        };

    }

    public static Iterable<String> getALLFiles(String dirFile, Boolean absolutePath) throws IOException {
        File file = new File(dirFile);
        if (file.isFile())
            return new Iterable<String>() {
                @Override
                public Iterator<String> iterator() {
                    return new Iterator<String>() {
                        boolean get = false;

                        @Override
                        public boolean hasNext() {
                            return !get;
                        }

                        @Override
                        public String next() {
                            get = true;
                            if (absolutePath)
                                try {
                                    return file.getCanonicalPath();
                                } catch (IOException e) {
                                    throw new RuntimeException(new IOException());
                                }
                            else
                                return file.getName();

                        }
                    };
                }
            };
        return new Iterable<String>() {
            String parentName = file.getCanonicalFile().getName();

            @Override
            public Iterator<String> iterator() {
                Iterable<File> data = Wrappers.wrapArr(new File(dirFile).listFiles());
                return new Iterator<String>() {
                    final Iterator<File> iter = data.iterator();
                    Iterator<String> smallIter;

                    @Override
                    public boolean hasNext() {
                        return (smallIter != null && smallIter.hasNext()) || iter.hasNext();
                    }

                    @Override
                    public String next() {
                        if (smallIter != null) {
                            if (smallIter.hasNext())
                                return returnName(new File(smallIter.next()));
                            else
                                smallIter = null;
                        }

                        File child = iter.next();
                        if (child.isDirectory()) {
                            try {
                                smallIter = getALLFiles(child.getCanonicalPath(), absolutePath).iterator();
                            } catch (IOException e) {
                                throw new RuntimeException(new IOException());
                            }
                            return next();
                        }
                        return returnName(new File(child.getName()));

                    }

                    String returnName(File child) {
                        if (absolutePath)
                            try {
                                return child.getCanonicalPath();
                            } catch (IOException e) {
                                throw new RuntimeException(new IOException());
                            }
                        else
                            return new File(parentName, child.getPath()).getPath();
                    }
                };
            }
        };
    }

}
