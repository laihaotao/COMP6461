package assignment2.response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-04
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpResponse {

    public String version;
    public int    code;
    public String status;
    public byte[] body;
    public HashMap<String, String> header = new HashMap<>();

    public HttpResponse(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        if (this.code == 200) this.status = "OK";
        else if (this.code == 404) this.status = "Not Found";
        else this.status = "Unknown";

        StringBuilder builder = new StringBuilder();
        builder.append(this.version).append(" ").append(this.code).append(" ").append(this.status);
        builder.append("\r\n");

        // add default header
        Date             today     = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yy hh:mm:ss z");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String output = formatter.format(today);
        this.header.put("Date", output);
        this.header.put("Accept", "*/*");
        this.header.put("Server", "COMP6461.com");

        for (String key : header.keySet()) {
            builder.append(key).append(": ").append(header.get(key)).append("\r\n");
        }
        builder.append("\r\n");
        return builder.toString();
    }
}
