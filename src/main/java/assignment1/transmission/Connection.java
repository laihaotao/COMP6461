package assignment1.transmission;

import assignment1.response.HttpResponse;
import assignment1.response.ResponseHeader;
import assignment1.response.ResponseLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Connection {

    private static final Logger logger = LoggerFactory.getLogger(Connection.class);
    private final int BUF_SIZE = 1024;

    // buffer_1 will be used both in send and receive message
    // buffer_2 will be only used in receive message
    private ByteBuffer buffer_1;
    private ByteBuffer buffer_2;
    private SocketChannel socket;

    public Connection() throws IOException {
        this.buffer_1 = ByteBuffer.allocate(BUF_SIZE);
        this.buffer_2 = ByteBuffer.allocate(BUF_SIZE);
    }

    public void send(String req, String host, int port) throws IOException {
        connect(host, port);
        socket.configureBlocking(true);
        Charset utf8 = StandardCharsets.UTF_8;
        byte[] request = req.getBytes(utf8);

        // if the request message larger than the buffer_1's size, we
        // have to cut them into pieces
        if (request.length > BUF_SIZE) {
            int round = request.length / BUF_SIZE + 1;
            for (int i = 0; i < round; i++) {
                buffer_1.clear();
                buffer_1.put(request, i * BUF_SIZE, BUF_SIZE);
                buffer_1.flip();
                socket.write(buffer_1);
                logger.debug("sending request message: {}", Arrays.toString(request));
                round--;
            }
        }
        // otherwise, put all of them into the buffer_1 and send them out
        else {
            buffer_1.put(request, 0, request.length);
            buffer_1.flip();
            socket.write(buffer_1);
//            logger.debug("buffer_1 content: {}", buffer_1.array());
            logger.debug("sending request message: {}", Arrays.toString(request));
        }
        buffer_1.clear();
    }

    public void receive() throws IOException {
        // the idea here is we need to read the header and parse them to
        // get the Content-Length's value and decide how many time I need
        // to read for the remaining message
        byte[] bytes = new byte[BUF_SIZE];
        HttpResponse httpResponse = new HttpResponse();

        // always begin with buffer_1
        ByteBuffer curBuffer = buffer_1;
        int readedLen = socket.read(curBuffer);
        if (readedLen < BUF_SIZE) {
            // it means all data are read in one shot
            // !!!!!!!! remember to FLIP !!!!!!!!
            curBuffer.flip();
            curBuffer.get(bytes, 0, readedLen);
            String data = new String(bytes, 0, readedLen);

            // split the string by "\r\n"
            String[] res = data.split("[\r\n]+");
            logger.debug("after splitting the response by '\r\n', we have {} part", res.length);
            if (res.length > 1) {
                // process the response line
                ResponseLine line = new ResponseLine(res[0]);
                httpResponse.setResponseLine(line);
                // process the response header
                ResponseHeader header = new ResponseHeader();
                int i = 1;
                for (; i < res.length; i++) {
                    String resHeader = res[i];
                    if (resHeader.contains(":")) {
                        String[] tmp = resHeader.split(":");
                        String key = tmp[0];
                        String value = tmp[1];
                        header.add(key, value);
                    } else {
                        logger.error("Invalid response header format");
                        break;
                    }
                }
                // it means all the remaining data is response body
                String bodyData = "";
                for (; i < res.length; i++) {
                    bodyData += res[i];
                }
                logger.debug("Response body data: {}", bodyData);

                // process the response body, if we have response body, their
                // must be a Content-Length in the header


                //int contentLen = Integer.parseInt(header.get("Content-Type"));


            }
        } else {
            // have to read multiple time
        }
    }

    private void connect(String host, int port) throws IOException {
        SocketAddress remote = new InetSocketAddress(host, port);
        socket = SocketChannel.open();
        socket.connect(remote);
        logger.debug("connect to: {}", socket.getRemoteAddress());
        logger.debug("local address: {}", socket.getLocalAddress());
    }
}
