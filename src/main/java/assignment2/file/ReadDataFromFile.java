package assignment2.file;

import ch.qos.logback.core.net.SyslogOutputStream;

import java.io.*;

/**
 * Created by heart on 2017-10-12.
 */
public class ReadDataFromFile {
//    public static String readFile1(String filename) throws IOException {
//        File sourceFile = new File(filename);
//        FileInputStream fileInputStream = new FileInputStream(sourceFile);
//        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
//        BufferedReader br = new BufferedReader(inputStreamReader);
//        StringBuilder content = new StringBuilder();
//        String line = new String();
//        while ((line = br.readLine()) != null) {
//
//            content.append(line+"\n");
//        }
//        br.close();
//        inputStreamReader.close();
//        fileInputStream.close();
//        String fileContent = content.toString();
//        return fileContent;
//    }
//
//    public static String readFile(File sourceFile) throws IOException {
//        FileReader fileReader = new FileReader(sourceFile);
//        BufferedReader br = new BufferedReader(fileReader);
//        StringBuilder content = new StringBuilder();
//        String line;
//        while ((line = br.readLine()) != null) {
//            content.append(line).append("\n");
//        }
//        br.close();
//        fileReader.close();
//        return content.toString();
//    }


    public static byte[] readFile(File sourceFile) throws IOException {
        byte[] buf = new byte[1024 * 1024];
        InputStream ins = new FileInputStream(sourceFile);
        int len = ins.read(buf);
        byte[] res = new byte[len];
        System.arraycopy(buf, 0, res, 0, len);
        return res;
    }


}

