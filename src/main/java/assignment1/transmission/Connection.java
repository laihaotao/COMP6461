package assignment1.transmission;

import assignment1.request.RequestBody;
import assignment1.response.HttpResponse;
import assignment1.response.ResponseBody;
import assignment1.response.ResponseHeader;
import assignment1.response.ResponseLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Connection {

    private static final Logger logger = LoggerFactory.getLogger(Connection.class);
    private final int BUF_SIZE = 1024;

    private ByteBuffer buffer;
    private SocketChannel socket;

    public Connection() throws IOException {
        this.buffer = ByteBuffer.allocate(BUF_SIZE);
    }

    public void send(String req, String host, int port) throws IOException {
        connect(host, port);
        socket.configureBlocking(true);
        Charset utf8 = StandardCharsets.UTF_8;
        byte[] request = req.getBytes(utf8);

        // if the request message larger than the buffer's size, we
        // have to cut them into pieces
        if (request.length > BUF_SIZE) {
            int round = request.length / BUF_SIZE + 1;
            for (int i = 0; i < round; i++) {
                buffer.clear();
                buffer.put(request, i * BUF_SIZE, BUF_SIZE);
                buffer.flip();
                socket.write(buffer);
                logger.debug("sending request message: {}", Arrays.toString(request));
                round--;
            }
        }
        // otherwise, put all of them into the buffer and send them out
        else {
            buffer.put(request, 0, request.length);
            buffer.flip();
            socket.write(buffer);
            logger.debug("sending request message: {}", Arrays.toString(request));
        }
        buffer.clear();
    }

    public HttpResponse receive() {
        // the idea here is we need to read the header and parse them to
        // get the Content-Length's value and decide how many time I need
        // to read for the remaining message
        HttpResponse response = null;
        ResponseLine line;
        ResponseHeader header;
        ResponseBody body;
        byte[] bytes = new byte[BUF_SIZE];
        ByteBuffer curBuffer = buffer;

        // read data into buffer
        int readedLen = 0;
        try {
            readedLen = socket.read(curBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        curBuffer.flip();
        // get data from buffer
        curBuffer.get(bytes, 0, readedLen);
        String data = new String(bytes, 0, readedLen);

        // split the string by "\r\n"
        String[] res = data.split("[\r\n]+");
        logger.debug("after splitting the response by '\r\n', we have {} part", res.length);
        if (res.length > 1) {
            // process the response line
            line = new ResponseLine(res[0]);
            // process the response header
            header = new ResponseHeader();
            int i = 1;
            for (; i < res.length; i++) {
                String resHeader = res[i];
                if (resHeader.contains(":")) {
                    String[] tmp = resHeader.split(":");
                    String key = tmp[0];
                    String value = tmp[1];
                    header.add(key, value);
//                    logger.debug("response header: {}:{}", key, value);
                } else {
                    logger.error("Invalid response header format");
                    break;
                }
            }
            // it means all the remaining data is response body
            StringBuilder bodyData = new StringBuilder();
            for (; i < res.length; i++) {
                bodyData.append(res[i]);
            }
            buffer.clear();

            try {
                while ((readedLen = socket.read(buffer)) != -1) {
                    curBuffer.flip();
                    // get data from buffer
                    curBuffer.get(bytes, 0, readedLen);
                    String data1 = new String(bytes, 0, readedLen);
                    bodyData.append(data1);
                    buffer.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            logger.debug("Response body data: {}", bodyData);
            body = new ResponseBody(bodyData.toString());
            response = new HttpResponse(line, header, body);
        }
        return response;
    }

    private void connect(String host, int port) throws IOException {
        SocketAddress remote = new InetSocketAddress(host, port);
        socket = SocketChannel.open();
        socket.connect(remote);
        logger.debug("connect to: {}", socket.getRemoteAddress());
        logger.debug("local address: {}", socket.getLocalAddress());
    }
}
