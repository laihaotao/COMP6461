package dev3.example2;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_READ;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-17
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class UDPClient {

    private DatagramChannel channel;
    private SocketAddress addr;

    public UDPClient(SocketAddress addr) throws IOException {
        channel = DatagramChannel.open();
        this.addr = addr;
    }

    public void send(String msg) throws IOException {
        channel.connect(addr);
        System.out.println(channel.getLocalAddress());
        channel.send(ByteBuffer.wrap(msg.getBytes()), addr);
    }

    public void recv() throws IOException {
        channel.configureBlocking(false);
        Selector selector = Selector.open();
        channel.register(selector, OP_READ);
        selector.select(3000);

        Set<SelectionKey> keys = selector.selectedKeys();
        if (!keys.isEmpty()) {
            ByteBuffer buf = ByteBuffer.allocate(1024);
            buf.flip();
            System.out.println(new String(buf.array()));
            buf.clear();
        }
    }

}
