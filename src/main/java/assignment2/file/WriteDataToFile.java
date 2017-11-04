package assignment2.file;
import java.io.*;
/**
 * Created by heart on 2017-10-12.
 */
public class WriteDataToFile {

    public static void writeData(String content, String path) throws IOException {
        File outputFile = new File(path);
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(content);
        bufferedWriter.close();
    }

}
