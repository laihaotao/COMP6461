package dev3.example1;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

public class UDPServer {

    private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);

    private void listenAndServe(int port) throws IOException {

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(false);
            logger.info("EchoServer is listening at {}", channel.getLocalAddress());
            ByteBuffer buf      = ByteBuffer.allocate(1024);
            Selector   selector = Selector.open();
            channel.register(selector, OP_READ);
            for (; ; ) {

                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                if (!keys.isEmpty()) {
                    for (SelectionKey key : keys) {
                        DatagramChannel c = (DatagramChannel) key.channel();
                        buf.clear();
                        SocketAddress addr = c.receive(buf);
                        buf.flip();
                        System.out.println("receive data: " + new String(buf.array()));
                        System.out.println("from: " + addr);
                    }
                    keys.clear();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        UDPServer server = new UDPServer();
        server.listenAndServe(8007);
    }
}