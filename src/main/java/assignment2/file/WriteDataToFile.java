package assignment2.file;
import java.io.*;
/**
 * Created by heart on 2017-10-12.
 */
public class WriteDataToFile {
    public static void  writeData(String content,String fileName) throws IOException {
        File outputFile = new File(fileName);
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(content);
        bufferedWriter.flush();
        bufferedWriter.close();
        fileWriter.close();
    }


    public static void main(String[] args) throws IOException {
        String content = "12345\n12345asdfg!@#$%";
        String fileName="outputFile.txt";
        WriteDataToFile.writeData(content,fileName);
    }
}
