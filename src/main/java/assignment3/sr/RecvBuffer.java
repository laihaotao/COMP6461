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

    private boolean       isLastPacket;
    private long          curMinNum;
    private long          endSeqNum;
    private Packet[]      window;
    private ChannelThread thread;
    private SocketAddress rounter;
    private Connection    connection;

    public RecvBuffer(ChannelThread thread, SocketAddress rounter, Connection connection) {
        this.thread = thread;
        this.buffer = new LinkedList<>();
        this.window = new Packet[this.WIN_SIZE];
        this.rounter = rounter;
        this.connection = connection;
        this.curMinNum = -2;
        this.endSeqNum = -3;
    }

    @Override
    protected void update(NoticeMsg msg, Packet packet) throws IOException {
        switch (msg) {
            case END_OF_DATA:
                this.endSeqNum = packet.getSequenceNumber();
                logger.debug("Receive an end of data packet, endSeqNum = # {}", this.endSeqNum);
            case DATA:
                logger.debug("RecvBuffer receive a packet, handling ...");
                Packet p = this.handleDataPacket(packet);
                SocketAddress addr = new InetSocketAddress(p.getPeerAddress(), p.getPeerPort());
                // ACK do not need to go through the thread since it doesn't need a timer
                this.thread.getChannel().send(p.toBuffer(), this.rounter);
                logger.debug("An ACK for packet #{} has been sent", packet.getSequenceNumber());
                break;

            case SYN_ACK:
                if (!(this.connection instanceof ServerConnection)) {
                    long initialSeqNum = packet.getSequenceNumber() + 1;
                    logger.info("RecvBuffer receive the initial seqNum #{}", initialSeqNum);
                    this.curMinNum  = initialSeqNum;
                }
                break;

            case SYN:
                if (this.connection instanceof ServerConnection) {
                    long initialSeqNum = packet.getSequenceNumber() + 1;
                    logger.info("RecvBuffer receive the initial seqNum #{}", initialSeqNum);
                    this.curMinNum  = initialSeqNum;
                }
                break;
        }
    }

    public int receive(ByteBuffer result) {
        if (this.endSeqNum == this.curMinNum) return -1;

        int len = 0;
        // when the user want to receive something, if the
        // buffer is empty, block the user thread here
        while (this.endSeqNum != this.curMinNum) {
            synchronized (this.buffer) {
                try {
                    this.buffer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!this.buffer.isEmpty()) {
            int    i   = 0;
            byte[] tmp = new byte[this.buffer.size()];
            for (byte b : this.buffer) {
                tmp[i++] = b;
            }
            len = tmp.length;
            result.clear();
            result.put(tmp);
            this.buffer.clear();
        }

        return len;
    }

    private Packet handleDataPacket(Packet p) {
        if (p.getType() == Packet.DATA) {
            long seqNum = p.getSequenceNumber();
            if (seqNum >= this.curMinNum && seqNum < this.curMinNum + this.WIN_SIZE) {
                // if the sequence number indicate that the packet is the one we
                // are waiting for, put into the buffer and if we have the check
                // the contiguous packets can give to the secondary buffer
                int idx = (int) (seqNum - this.curMinNum);
                this.window[idx] = p;
                this.checkAndMove();
            } else {
                logger.debug("Already received it or not expecting packet # {}", seqNum);
            }
            // generate an ACK for that packet and send it back to the sender
            // no matter the packet is the one we are waiting for or not
        }
        synchronized (this.buffer) {
            this.buffer.notify();
        }
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
                break;
            } else {
                // otherwise, add the payload to the buffer
                for (byte b : this.window[idx].getPayload()) {
                    if (b == 0) break;
                    this.buffer.add(b);
                }
                this.curMinNum += 1;
            }
        }
    }

}
