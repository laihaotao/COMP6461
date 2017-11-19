package assignment3;

import assignment3.observer.NoticeMsg;
import assignment3.observer.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_READ;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-17
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ChannelThread extends Subject implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ChannelThread.class);

    private final List<Packet> queue;

    private SocketAddress   rtrAddr;
    private DatagramChannel channel;
    private Selector        selector;
    private Window          window;

    public ChannelThread(DatagramChannel channel, SocketAddress rtrAddr) {
        this.rtrAddr   = rtrAddr;
        this.channel   = channel;
        this.queue     = new LinkedList<>();
        this.window    = new Window(this.queue);
        this.attach(this.window);
    }

    @Override
    public void run() {
        try {
            this.channel.configureBlocking(false);
            this.selector = Selector.open();
            this.channel.register(this.selector, OP_READ);
            while (true) {

                if (this.window.getHasSth2Send() > 0) {
                    SelectionKey key = this.channel.keyFor(this.selector);
                    key.interestOps(SelectionKey.OP_WRITE);
                }

                this.selector.select(500);
                Set<SelectionKey> keys = selector.selectedKeys();
                if (!keys.isEmpty()) {
                    for (SelectionKey k : keys) {
                        if (k.isReadable()) {
                            this.read(k);
                        } else if (k.isWritable()) {
                            this.write(k);
                        }
                    }
                    keys.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(SelectionKey k) throws IOException {
        DatagramChannel channel = (DatagramChannel) k.channel();
        ByteBuffer      buf     = ByteBuffer.allocate(Packet.MAX_LEN);
        SocketAddress   router  = channel.receive(buf);
        buf.flip();
        Packet resp = Packet.fromBuffer(buf);
        logger.info("Packet: {}", resp);
        logger.info("Router: {}", router);
        String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
        logger.info("Payload: {}",  payload);

        // todo: parse the header to see the type of the packet
        this.handler(resp);
    }

    private void handler(Packet packet) {
        int type = packet.getType();
        switch (type) {
            // ** if the packet is a handshaking packet, tell the connection
            case Packet.SYN_2:
                this.notifyObservers(NoticeMsg.SYN_ACK, packet);
                break;
            case Packet.SYN_3:
                this.notifyObservers(NoticeMsg.SYN_ACK_ACK, packet);
                break;
            // ** end of handshaking packet handler

            // if the packet is a data ack, tell the window
            case Packet.ACK:
                this.notifyObservers(NoticeMsg.ACK, packet);
                break;

            // todo: if the packet is the data, tell the receiver buffer
            case Packet.DATA:
            this.notifyObservers(NoticeMsg.DATA, packet);
            break;
        }
    }

    private void write(SelectionKey k) throws IOException {
        DatagramChannel c = (DatagramChannel) k.channel();
        synchronized (queue) {
            int i = 0;
            int time = this.window.getHasSth2Send();
            while (time > 0) {
                c.send(this.queue.get(i).toBuffer(), this.rtrAddr);
                time--;
            }
            this.window.setHasSth2Send(time);
            // todo: start a timer for each packet
        }
        k.interestOps(OP_READ);
    }

    public void send(Packet[] packets) {
        synchronized (this.queue) {
            this.queue.addAll(Arrays.asList(packets));
            this.notifyObservers(NoticeMsg.WIN_CHECK, null);
        }
    }

    public void sendHandshakePacket(Packet packet) {
        synchronized (this.queue) {
            this.queue.add(packet);
            this.notifyObservers(NoticeMsg.WIN_CHECK, null);
        }
    }

}

