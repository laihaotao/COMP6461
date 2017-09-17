package assignment1.file;

import assignment1.request.RequestLine;

import java.io.*;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-17
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class DataFileReader {

    private File file;
    private BufferedReader reader;

    public DataFileReader(String path) throws FileNotFoundException {
        this.file = new File(path);
        this.reader = new BufferedReader(new FileReader(file));
    }

    public String read() throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
}
