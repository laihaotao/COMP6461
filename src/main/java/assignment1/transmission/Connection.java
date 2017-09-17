package assignment1.transmission;

import assignment1.common.ParamHolder;
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
import java.util.Properties;

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

    public Connection(String host, int port) throws IOException {
        this.buffer = ByteBuffer.allocate(BUF_SIZE);
        connect(host, port);
    }

    public Connection(ParamHolder holder) throws IOException {
        this.buffer = ByteBuffer.allocate(BUF_SIZE);
        connect(holder.host, 80);
    }

    public void send(String req) throws IOException {
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
//            logger.debug("buffer content: {}", buffer.array());
            logger.debug("sending request message: {}", Arrays.toString(request));
        }
        buffer.clear();
    }

    public void receive() {

    }

    private void connect(String host, int port) throws IOException {
        SocketAddress remote = new InetSocketAddress(host, port);
        socket = SocketChannel.open();
        socket.connect(remote);
        logger.debug("connect to: {}", socket.getRemoteAddress());
        logger.debug("local address: {}", socket.getLocalAddress());
    }
}
