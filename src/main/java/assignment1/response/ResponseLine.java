package assignment1.response;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-23
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ResponseLine {

    private String version;
    private int statusCode;
    private String statusMsg;

    public ResponseLine(String line) {
        String[] parts = line.split("\\s+");
        this.version = parts[0];
        this.statusCode = Integer.parseInt(parts[1]);
        this.statusMsg = parts[2];
    }

    @Override
    public String toString() {
        return version + " " + statusCode + " " + statusMsg;
    }
}
