package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import Tools.Tools_funct;
import Tools.Conv.Wrappers;
import huff.Huff;

public class test_huff {
    final static String fileName = "test/example_files/test_text.txt";
    final static String resultFile = "test/example_files/result.huff";
    final static String resultPath = ".";
    
    static void test_compression() throws IOException {
        Huff.transferDataFile(new String[] { fileName }, resultFile);
    }

    static void testDecompression() throws IOException {
        FileInputStream file = new Tools_funct.GetDataBytes(resultFile);
        Huff.retransferDataFile(Wrappers.wrapStream(file), resultPath);
    }

    static void testCompressionPython() throws IOException {
        FileInputStream file = new Tools_funct.GetDataBytes(
                "G:/Learning/Learning_python/Advanced/Data_structures/Data_compression_implementation/trans.huff");
        // Iterable<Byte> iterD=Wrappers.wrapStream(file);
        Huff.retransferDataFile(Wrappers.wrapStream(file), "./");
        // System.out.println(Size.getSizeLong(iterD));
    }

    static void testEncoding() throws IOException {
        HashMap<Byte, String> hash;
        try (FileInputStream file = new FileInputStream(fileName)) {
            hash = Huff.get_codec(Wrappers.wrapStream(file));
            try (FileOutputStream filew = new FileOutputStream("codec_init.huff")) {
                for (byte b : Huff.encodeCodec(hash))
                    filew.write(b);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        test_compression();
        //testDecompression();
        // testEncoding();
    }

}
