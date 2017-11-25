package assignment1.transmission;

import assignment1.common.ParamHolder;
import assignment1.response.HttpResponse;
import assignment1.response.ResponseBody;
import assignment1.response.ResponseHeader;
import assignment1.response.ResponseLine;
import assignment3.RUDP.ClientRUDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Cleaner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Connection {

    private static final Logger logger = LoggerFactory.getLogger(Connection.class);
    private final int BUF_SIZE = 8192;

    private ClientRUDP client;
    private String     fileName;
    private boolean    isVerbose;

    public Connection(ParamHolder holder) throws IOException {
        if (holder.hasOutputFile) {
            this.fileName = holder.outputFileName;
        }
        this.isVerbose = holder.isVerbose;
        client = new ClientRUDP(8098, holder.host, Integer.parseInt(holder.port));
    }

    public void send(String req) throws IOException {
        client.send(req);
    }

    public HttpResponse receive() {
        // the idea here is we need to read the header and parse them to
        // get the Content-Length's value and decide how many time I need
        // to read for the remaining message
        ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
        HttpResponse response = null;
        StringBuilder bodyData = null;
        ResponseLine line;
        ResponseHeader header;
        ResponseBody body;
        byte[] bytes = new byte[BUF_SIZE];

        buffer.clear();
        // read data into buffer
        int readedLen = 0;
        readedLen = client.receive(buffer);
        buffer.flip();
        // get data from buffer
        buffer.get(bytes, 0, readedLen);
        String data = new String(bytes, 0, readedLen);

        // split the string by "\r\n"
        String[] res = data.split("[\r\n]+");
        logger.trace("after splitting the assignment2.response by '\\r\\n', we have {} part", res.length);
        if (res.length > 1) {
            // process the assignment2.response line
            line = new ResponseLine(res[0]);
            logger.trace("assignment2.response line: {}", line.toString());
            // process the assignment2.response header
            header = new ResponseHeader();
            int i = 1;
            for (; i < res.length; i++) {
                String resHeader = res[i];
                if (resHeader.contains(":")) {
                    int idx = resHeader.indexOf(':');
                    String key = resHeader.substring(0, idx).trim();
                    String value = resHeader.substring(idx + 1).trim();
                    header.add(key, value);
                    logger.trace("assignment2.response header: {}:{}", key, value);
                } else {
                    break;
                }
            }
            // it means all the remaining data is assignment2.response fileBody
            bodyData = new StringBuilder();
            for (; i < res.length; i++) {
                bodyData.append(res[i]);
            }
            buffer.clear();

            while ((readedLen = client.receive(buffer)) != -1) {
                buffer.flip();
                // get data from buffer
                buffer.get(bytes, 0, readedLen);
                String data1 = new String(bytes, 0, readedLen);
                bodyData.append(data1);
                buffer.clear();
            }

            body = new ResponseBody(bodyData.toString());

            if (line.checkRedirection()) {
                String location = header.get("Location");
                logger.debug("redirect to {}", location);
                response = new HttpResponse(true, location, line, header, body);
            } else {
                response = new HttpResponse(line, header, body);
            }
        }

        if (bodyData != null && fileName != null) {
            // need to write the fileBody to a separate file
            outputToFile(bodyData.toString());
        }

        if (isVerbose && response != null) System.out.println(response.toString());
        return response;
    }

    private void outputToFile(String body) {
        File file = new File("./" + fileName);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(body);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
