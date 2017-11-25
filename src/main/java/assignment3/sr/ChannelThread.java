package assignment3.sr;

import assignment3.RUDP.Packet;
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

    private Connection      connection;
    private SocketAddress   router;
    private DatagramChannel channel;
    private Selector        selector;
    private Window          window;

    public ChannelThread(DatagramChannel channel, SocketAddress router) throws IOException {
        this.router     = router;
        this.channel    = channel;
        this.queue      = new LinkedList<>();
        this.window     = new Window(this.queue, this);
        this.attach(this.window);
    }

    @Override
    public void run() {
        try {
            this.channel.configureBlocking(false);
            this.selector = Selector.open();
            this.channel.register(this.selector, OP_READ);
            while (true) {

                if (this.connection != null && this.connection.isConnected()
                    && this.window.getHasSth2Send() > 0) {
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
        printPacketDetail(resp, true);

        // parse the header to see the type of the packet
        this.handler(resp);
    }

    private void printPacketDetail(Packet resp, boolean isRecv) {
        if (isRecv) {
            logger.debug("// recv Packets ************************** //");
        }
        else {
            logger.debug("// send Packets ************************** //");
        }
            logger.debug("Packet: {}", resp);
        String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
        logger.debug("Payload: {}",  payload);
        logger.debug("// ****************************************** //");
    }

    private void handler(Packet packet) {
        int type = packet.getType();
        switch (type) {
            // ** if the packet is a handshaking packet, tell the connection
            case Packet.SYN_1:
                this.notifyObservers(NoticeMsg.SYN, packet);
                break;
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

            // if the packet is the data, tell the receiver buffer
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
                Packet p = this.queue.get(i);
                c.send(p.toBuffer(), this.router);
                printPacketDetail(p, false);
                // start the timer thread
                new Thread(this.window.getTimerMap().get(p.getSequenceNumber())).start();
                time--;
                i++;
            }
            this.window.setHasSth2Send(time);
        }
        k.interestOps(OP_READ);
    }

    public void send(byte[] message) throws IOException {
        // break the message into chunks
        Packet[] packets = this.connection.makeChunks(message);
        // put all chunks into the SenderThread buffer
        this.send(packets);
    }

    public void send(Packet packet) {
        synchronized (this.queue) {
            this.queue.add(packet);
            logger.debug("Add packet #{} to the queue", packet.getSequenceNumber());
            this.notifyObservers(NoticeMsg.WIN_CHECK, packet);
        }
    }

    private void send(Packet[] packets) {
        synchronized (this.queue) {
            for (Packet p : packets) {
                this.queue.add(p);
                logger.debug("Add packet #{} to the queue", p.getSequenceNumber());
                this.notifyObservers(NoticeMsg.WIN_CHECK, p);
            }
        }
    }

    public void send(int index, Packet packet) {
        synchronized (this.queue) {
            this.queue.add(index, packet);
            logger.debug("Add packet #{} to the queue at position {}", packet.getSequenceNumber(), index);
            this.notifyObservers(NoticeMsg.WIN_CHECK, packet);
        }
    }

    public DatagramChannel getChannel() {
        return channel;
    }

    public SocketAddress getRouter() {
        return router;
    }

    public void bind(Connection connection) {
        this.connection = connection;
    }
}

