package dev3.example1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

public class UDPServer {

    private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);

    public UDPServer() { }

    private void listenAndServe(int port) throws IOException {

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(port));
            logger.info("EchoServer is listening at {}", channel.getLocalAddress());
            ByteBuffer buf = ByteBuffer
                    .allocate(Packet.MAX_LEN)
                    .order(ByteOrder.BIG_ENDIAN);

            for (; ; ) {
                buf.clear();
                SocketAddress router = channel.receive(buf);

                // Parse a packet from the received raw data.
                buf.flip();
                Packet packet = Packet.fromBuffer(buf);
                buf.flip();

                String payload = new String(packet.getPayload(), UTF_8);
                logger.info("Packet: {}", packet);
                logger.info("Payload: {}", payload);
                logger.info("Router: {}", router);
            }
        }
    }

//    private ByteBuffer readPartOfFile() {
//        byte[] buf = new byte[BUF_SIZE];
//        try {
//            int len = is.read(buf);
//            this.isLastPart = len == BUF_SIZE;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ByteBuffer.wrap(buf);
//    }

    public static void main(String[] args) throws IOException {
        UDPServer server = new UDPServer();
        server.listenAndServe(8007);
    }
}