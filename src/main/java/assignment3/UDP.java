package assignment3;

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
 * Date:    2017-11-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class UDP {

    private static final int TIMEOUT = 2000;

    public static void send(DatagramChannel c, ByteBuffer buf, SocketAddress addr) throws IOException {
        c.send(buf, addr);
    }

    public static long recvSynAck(DatagramChannel c) throws IOException {
        c.configureBlocking(false);
        Selector selector = Selector.open();
        c.register(selector, OP_READ);
        selector.select(TIMEOUT);

        Set<SelectionKey> keys = selector.selectedKeys();
        if (!keys.isEmpty()) {
            ByteBuffer buf = ByteBuffer.allocate(Packet.MIN_LEN);
            buf.flip();
            Packet recvPacket = Packet.fromBuffer(buf);

            if (recvPacket.getType() == Packet.SYN_2) {
                // if the packet is a SYN_2 packet means the receiver
                // acknowledge the sender's sequence + 1 and the packet's
                // sequenceNumber field is the receiver's beginning sequence number
                long seqNum = recvPacket.getSequenceNumber();
                keys.clear();
                return seqNum;
            }
        }
        return -1;
    }

    public static boolean recvSynAckAck(DatagramChannel c, long expectedSeqNum) throws IOException {
        c.configureBlocking(false);
        Selector selector = Selector.open();
        c.register(selector, OP_READ);
        selector.select(TIMEOUT);

        Set<SelectionKey> keys = selector.selectedKeys();
        if (!keys.isEmpty()) {
            ByteBuffer buf = ByteBuffer.allocate(Packet.MIN_LEN);
            buf.flip();
            Packet recvPacket = Packet.fromBuffer(buf);

            if (recvPacket.getType() == Packet.SYN_3) {

                keys.clear();
                return expectedSeqNum == recvPacket.getSequenceNumber();
            }
        }
        return false;
    }


    public static boolean recvDataAck(DatagramChannel c, long waitingAckSeqNum) throws IOException {
        c.configureBlocking(false);
        Selector selector = Selector.open();
        c.register(selector, OP_READ);
        selector.select(TIMEOUT);

        Set<SelectionKey> keys = selector.selectedKeys();
        if (!keys.isEmpty()) {
            ByteBuffer buf = ByteBuffer.allocate(Packet.MIN_LEN);
            buf.flip();
            Packet recvPacket = Packet.fromBuffer(buf);

            if (recvPacket.getType() == Packet.ACK) {
                // if the packet is a ACK packet means the receiver
                // acknowledge the sender's sequence number
                long num = recvPacket.getSequenceNumber();
                keys.clear();
                return num == waitingAckSeqNum;
            }
        }
        return false;
    }
}
