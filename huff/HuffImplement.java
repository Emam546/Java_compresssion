package huff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import binaryTree.Basic_Tree;
import Tools.BytesArr;
import Tools.Conv.Converter;
import Tools.Tools_funct.Pair;

public class HuffImplement extends init_file {
    static class CODEC extends HashMap<Byte, String> {
        public float entropy;
        private HashMap<Byte, Long> probabilities = new HashMap<Byte, Long>();

        public CODEC() {
            super();
        }

        public CODEC(int initialCapacity) {
            super(initialCapacity);
        }

        public CODEC(Map<? extends Byte, ? extends String> m) {
            super(m);
        }

        public CODEC(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        private void add(byte bytes) {
            long val = 0;
            if (probabilities.containsKey(bytes)) {
                val = probabilities.get(bytes);
            }
            val += 1;
            probabilities.put(bytes, val);
        }

        public void initialize(Iterable<Byte> data) {
            this.clear();
            probabilities.clear();
            entropy = 0;
            System.out.println("READING THE FILE");
            for (byte b : data)
                add(b);
            float size = 0;
            for (long val : probabilities.values())
                size += val;
            if (probabilities.size() == 0)
                ;

            else if (probabilities.size() == 1) {
                this.put(probabilities.keySet().iterator().next(), "0");
                entropy = 1;
            } else if (size == 0) {
                this.put(probabilities.keySet().iterator().next(), "0");
                throw new RuntimeException(this.toString());
            } else {
                List<HuffManTree<Byte>> trees = new ArrayList<HuffManTree<Byte>>();

                for (Byte byte1 : probabilities.keySet())
                    trees.add(new HuffManTree<Byte>(probabilities.get(byte1) / size, byte1));

                List<HuffManTree<Byte>> props = new ArrayList<>(trees);
                while (trees.size() != 1) {
                    trees.sort((a, b) -> b.compareTo(a));
                    HuffManTree<Byte> right = trees.get(trees.size() - 1);
                    HuffManTree<Byte> left = trees.get(trees.size() - 2);
                    float key = left.key + right.key;
                    HuffManTree<Byte> newNode = new HuffManTree<Byte>(key, left, right);
                    for (Basic_Tree<Byte> b : Arrays.asList(left, right)) {
                        b.parent = newNode;
                        trees.remove(trees.size() - 1);
                    }
                    trees.add(newNode);
                }

                for (HuffManTree<Byte> byte1 : props) {
                    if (byte1 != null) {
                        this.put((Byte) byte1.getValue(), byte1.repreBits());
                        entropy += byte1.repreBits().length() * byte1.key;
                    }
                }
            }
        }
    }

    public static CODEC get_codec(Iterable<Byte> data) {
        CODEC code = new CODEC();
        code.initialize(data);
        return code;
    }

    public static Iterable<Byte> transferData(Iterable<Byte> data, Map<Byte, String> codec) {
        return new Iterable<Byte>() {
            @Override
            public Iterator<Byte> iterator() {
                return new Iterator<Byte>() {
                    boolean finished = true;
                    String code = "";
                    List<String> bitsCodecs = new ArrayList<>();
                    final Iterator<Byte> iter = data.iterator();
                    {
                        for (String s : codec.values())
                            bitsCodecs.add(s);
                    }

                    @Override
                    public boolean hasNext() {
                        return finished;
                    }

                    byte getByte() {
                        int byte_code = Integer.parseInt(code.substring(0, 8), 2);
                        code = code.substring(8);
                        return (byte) (byte_code);
                    }

                    @Override
                    public Byte next() {
                        if (code.length() >= 8)
                            return getByte();
                        while (iter.hasNext()) {
                            Byte byte1 = iter.next();
                            code += codec.get(byte1);
                            if (code.length() < 8)
                                continue;
                            byte res = getByte();
                            if (code.length() == 0 && !iter.hasNext())
                                finished = false;

                            return res;
                        }

                        if (code.length() != 0) {
                            if (code.length() < 8) {
                                String newCode;
                                while (true) {
                                    newCode = Integer.toBinaryString((int) (Math.random() * 255));
                                    newCode = "00000000".substring(newCode.length()) + newCode;
                                    newCode = newCode.substring(code.length());
                                    Boolean continues = false;
                                    for (int i = 0; i < newCode.length()+1; i++) {
                                        if (bitsCodecs.contains(newCode.substring(0, i))) {
                                            continues = true;
                                            break;
                                        }

                                    }
                                    if (!continues)
                                        break;
                                }
                                code = code + newCode;
                            }
                            byte res = getByte();
                            if (code.length() == 0)
                                finished = false;
                            return res;
                        }
                        throw new NoSuchElementException();

                    }

                };
            }

        };
    }

    public static Iterable<Byte[]> retransferData(Iterable<Byte> data, Map<Byte, String> codec) {
        return new Iterable<Byte[]>() {
            @Override
            public Iterator<Byte[]> iterator() {

                return new Iterator<Byte[]>() {
                    HashMap<String, Byte> map = new HashMap<String, Byte>();
                    final Iterator<Byte> iter = data.iterator();
                    String last_code = "";
                    {
                        for (Byte byte1 : codec.keySet())
                            map.put(codec.get(byte1), byte1);
                    }

                    private Pair<String, Byte[]> search(byte byte1, String last_code) {
                        String byteS = Integer.toBinaryString(Converter.byte2int(byte1));
                        String _binary = last_code + "00000000".substring(byteS.length()) + byteS;
                        return getresult(_binary);
                    }

                    private Pair<String, Byte[]> getresult(String binary) {
                        BytesArr result_bytes = new BytesArr();
                        int start = 0;
                        for (int end = 0; end < binary.length() + 1; end++) {
                            if (map.containsKey(binary.substring(start, end))) {
                                result_bytes.add(map.get(binary.substring(start, end)));
                                start = end;
                            }
                        }
                        return new Pair<String, Byte[]>(binary.substring(start), result_bytes.toArray());
                    }

                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public Byte[] next() {
                        Pair<String, Byte[]> res = search(iter.next(), last_code);
                        last_code = res.key;
                        return res.value;
                    }
                };
            }
        };
    }

}
