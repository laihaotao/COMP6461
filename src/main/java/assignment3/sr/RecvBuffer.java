package assignment3.sr;

import assignment3.RUDP.Packet;
import assignment3.observer.NoticeMsg;
import assignment3.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-19
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class RecvBuffer extends Observer{

    private static final Logger logger = LoggerFactory.getLogger(RecvBuffer.class);

    private final int WIN_SIZE = 4;
    private final List<Byte> buffer;

    private long          curMinNum;
    private Packet[]      window;
    private ChannelThread thread;
    private SocketAddress rounter;

    public RecvBuffer(ChannelThread thread, SocketAddress rounter) {
        this.thread = thread;
        this.buffer = new LinkedList<>();
        this.window = new Packet[this.WIN_SIZE];
        this.rounter = rounter;
    }

    @Override
    protected void update(NoticeMsg msg, Packet packet) throws IOException {
        if (msg == NoticeMsg.DATA) {
            logger.debug("RecvBuffer receive a packet, handling ...");
            Packet p = this.handleDataPacket(packet);
            SocketAddress addr = new InetSocketAddress(p.getPeerAddress(), p.getPeerPort());
            // ACK do not need to go through the thread since it doesn't need a timer
            this.thread.getChannel().send(p.toBuffer(), this.rounter);
            logger.debug("An ACK for packet #{} has been sent", packet.getSequenceNumber());
        } else if (msg == NoticeMsg.SYN) {
            long initialSeqNum = packet.getSequenceNumber() + 1;
            logger.debug("RecvBuffer receive the initial seqNum #{}", initialSeqNum);
            this.curMinNum = initialSeqNum;
        }
    }

    public int receive(ByteBuffer result) {
        // when the user want to receive something, if the
        // buffer is empty, block the user thread here
        if (this.buffer.isEmpty()) {
            synchronized (this.buffer) {
                try {
                    this.buffer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        int    i   = 0;
        byte[] tmp = new byte[this.buffer.size()];
        for (byte b : this.buffer) {
            tmp[i++] = b;
        }
        int len = tmp.length;
        result.clear();
        result.put(tmp);
        this.buffer.clear();
        return len;
    }

    private Packet handleDataPacket(Packet p) {
        long seqNum = p.getSequenceNumber();
        if (seqNum >= this.curMinNum && seqNum < this.curMinNum + this.WIN_SIZE) {
            // if the sequence number indicate that the packet is the one we
            // are waiting for, put into the buffer and if we have the check
            // the contiguous packets can give to the secondary buffer
            int idx = (int) (seqNum - this.curMinNum);
            this.window[idx] = p;
        }
        this.checkAndMove();
        synchronized (this.buffer) {
            this.buffer.notify();
        }
        // generate an ACK for that packet and send it back to the sender
        // no matter the packet is the one we are waiting for or not
        return constructAckPacket(p);
    }

    private Packet constructAckPacket(Packet p) {
        logger.debug("Generate a ACK for packet #{}", p.getSequenceNumber());
        return new Packet.Builder()
                .setType(Packet.ACK)
                .setSequenceNumber((p.getSequenceNumber()))
                .setPortNumber(p.getPeerPort())
                .setPeerAddress(p.getPeerAddress())
                .setPayload("".getBytes())
                .create();
    }

    private void checkAndMove() {
        int idx = 0;
        for (; idx < this.WIN_SIZE; idx++) {
            // if the idx packet is null means not continuous
            if (this.window[idx] == null) {
                this.curMinNum += idx;
                break;
            }
            // otherwise, add the payload to the buffer
            byte[] payload = this.window[idx].getPayload();
            for (byte b : payload) {
                if (b == 0) break;
                this.buffer.add(b);
            }
        }
    }

}
