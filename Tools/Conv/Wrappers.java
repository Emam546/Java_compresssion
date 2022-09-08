package Tools.Conv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;

/**
 * Wrappers
 */
public class Wrappers {
    public static <T> Iterable<T> wrapIter(Iterator<T> iter) {
        return () -> iter;
    }

    public static <T> Iterable<T> wrapIterArr(Iterable<T[]> data) {
        return () -> {
            return new Iterator<T>() {
                Iterator<T[]> iter = data.iterator();
                ArrayList<T> currArr = new ArrayList<T>();

                @Override
                public boolean hasNext() {
                    return currArr.size() != 0 || iter.hasNext();
                }

                @Override
                public T next() {
                    if (currArr.size() != 0)
                        return currArr.remove(0);
                    for (T obj : iter.next())
                        currArr.add(obj);
                    return next();
                }

            };
        };
    }

    public static Iterable<Integer> wrapByte(Iterable<Byte> data) {
        return () -> new Iterator<Integer>() {
            Iterator<Byte> iter = data.iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public Integer next() {
                return Converter.byte2int(iter.next());
            }

        };
    }

    public static Iterable<Byte> wrapInteger(Iterable<Integer> data) {
        return () -> {
            return new Iterator<Byte>() {
                Iterator<Integer> iter = data.iterator();

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public Byte next() {
                    return iter.next().byteValue();
                }
            };

        };
    }

    public static Iterable<Byte> wrapStream(InputStream data) {
        return () -> {
            return new Iterator<Byte>() {
                private Integer lastByte;
                private boolean hasnextB = false;

                @Override
                public boolean hasNext() {
                    try {
                        if (hasnextB)
                            return hasnextB;
                        return (hasnextB = ((lastByte = data.read()) >= 0));
                    } catch (IOException e) {
                        throw new RuntimeException(new IOException());
                    }
                }

                @Override
                public Byte next() {
                    if (hasnextB) {
                        hasnextB = false;
                        return lastByte.byteValue();
                    }
                    
                    throw new NoSuchElementException();

                }
            };

        };

    }

    public static <T> Iterable<T> wrapArr(T[] arr) {
        return () -> {
            return new Iterator<T>() {
                int count = 0;

                @Override
                public boolean hasNext() {
                    return count < arr.length;
                }

                @Override
                public T next() {
                    return arr[count++];
                }
            };
        };

    }
}