package huff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import Tools.Tools_funct;
import Tools.Conv.Converter;
import Tools.Conv.Wrappers;

public class Huff extends HuffImplement {
    public static void transferDataFile(String[] files, String resultFile) throws IOException {
        long org_size = 0;
        FileOutputStream resFile = new FileOutputStream(resultFile);
        ArrayList<String> AllDirs=new ArrayList<>();
        for (String string : files) 
            AllDirs.add(string);
        AllDirs.removeIf((o)->new File(o).isFile());
        AllDirs.replaceAll((o)->new File(o).getName());
        for (Byte[] bs : Tools_funct.encodeAllPaths(AllDirs))
            resFile.write(Converter.Bytes2bytes(bs));
        for (String dirFile : files) {
            Iterator<String> filesdir = Tools_funct.getALLFiles(dirFile, false).iterator();
            Iterator<String> filesCompletePath = Tools_funct.getALLFiles(dirFile, true).iterator();
            while (filesdir.hasNext()) {
                long cursor_now = resFile.getChannel().position();
                String file = filesCompletePath.next();
                org_size += new File(file).length();
                String fileName = filesdir.next();
                Iterable<Byte> fileout = ()-> {
                    try {
                        return Wrappers.wrapStream(new Tools_funct.GetDataBytes(file)).iterator();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(new FileNotFoundException());
                    }
                };
                Map<Byte, String> codec = get_codec(fileout);
                long sizeofbytes = 0;
                for (Byte b : encodeInitCompressData(0, fileName, codec))
                    resFile.write(b);
                
                System.out.println("\nCOMPRESSING HTE FILE");
                for (Byte b : transferData(fileout, codec)) {
                    resFile.write(b);
                    sizeofbytes += 1;
                }
                resFile.getChannel().position(cursor_now);
                resFile.write(Converter.num2Bytes(sizeofbytes));
                resFile.getChannel().position(resFile.getChannel().size());
            }
            System.out.println("\n");
            resFile.close();
            double result_size = (double)new File(resultFile).length();
            int num=Math.round(100 - ((float)(result_size / org_size) * 100));
            System.out.println("the File reduced by " + num + "%");
        }

    }

    public static void retransferDataFile(Iterable<Byte> data, String resultPath) throws IOException {
        Iterator<Byte> iter = data.iterator();
        Iterable<Byte>iterD=Wrappers.wrapIter(iter);
        resultPath=new File(resultPath).getCanonicalPath();
        for (String dir:Tools_funct.decodeAllPaths(iterD))
            new File(resultPath,dir).mkdirs();
        
        while (iter.hasNext()) {
            DecodeResult resc = decodeInitCompressData(iterD);
            String path=new File(resultPath, resc.filePath).getAbsolutePath();
            
            FileOutputStream retransferData = new FileOutputStream(path);
            Iterable<Byte> encodedData = new Iterable<Byte>() {
                int current = 0;
                @Override
                public Iterator<Byte> iterator() {
                    return new Iterator<Byte>() {
                        @Override
                        public boolean hasNext() {
                            return iter.hasNext() && current <= resc.size;
                        }

                        @Override
                        public Byte next() {
                            current++;
                            return iter.next();
                        }

                    };
                }
            };

            for (Byte[] bs : retransferData(encodedData, resc.codec))
                retransferData.write(Converter.Bytes2bytes(bs));

            retransferData.close();
            System.out.println("\n");
        }

    }

}
