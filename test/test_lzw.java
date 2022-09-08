package test;

import java.io.IOException;

import Tools.Tools_funct;
import Tools.Conv.Wrappers;
import lz77.lz78.LZ78;

public class test_lzw {
    final static String fileName = "test/example_files/test_text.txt";
    final static String resultFile = "test/example_files/result.lz78";
    final static String resultPath = "./test/ResultFile";

    static void test_compression() throws IOException {
        LZ78.transferDataFile(new String[] { fileName }, resultFile, null);
    }

    static void testDecompression() throws IOException {
        Iterable<Byte> file = Wrappers.wrapStream(new Tools_funct.GetDataBytes(resultFile));
        LZ78.retransferDataFile(file, resultPath);
    }

    public static void main(String[] args) throws IOException {
        test_compression();
        testDecompression();
    }
}
