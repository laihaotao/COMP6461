package assignment3.sr;

import assignment3.RUDP.Packet;
import assignment3.observer.NoticeMsg;
import assignment3.observer.Observer;

import java.io.IOException;
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

    private final int WIN_SIZE = 4;

    private long          curMinNum;
    private long          continueSeqNum;
    private List<Byte>    buffer;
    private Packet[]      window;
    private ChannelThread thread;

    public RecvBuffer(ChannelThread thread) {
        this.thread = thread;
        this.buffer = new LinkedList<>();
        this.window = new Packet[this.WIN_SIZE];
    }

    @Override
    protected void update(NoticeMsg msg, Packet packet) throws IOException {
        if (msg == NoticeMsg.DATA) {
            Packet p = this.handleDataPacket(packet);
            this.thread.send(p);
        }
    }

    public int receive(ByteBuffer result) {
        if (this.buffer.isEmpty()) return -1;
        int    i   = 0;
        byte[] tmp = new byte[this.buffer.size()];
        for (byte b : this.buffer) {
            tmp[i++] = b;
        }
        this.buffer.clear();
        result.put(tmp);
        return this.buffer.size();
    }

    private Packet handleDataPacket(Packet p) {
        long seqNum = p.getSequenceNumber();
        if (seqNum >= this.curMinNum && seqNum < this.curMinNum + this.WIN_SIZE) {
            // if the sequence number indicate that the packet is the one we
            // are waiting for, put into the buffer and  if we have the check
            // the contiguous packets can give to the secondary buffer
            int idx = (int) (seqNum - curMinNum);
            window[idx] = p;
        }
        this.checkAndMove();
        // generate an ACK for that packet and send it back to the sender
        // no matter the packet is the one we are waiting for or not
        return constructAckPacket(p);
    }

    private Packet constructAckPacket(Packet p) {
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
            if (this.window[idx] == null) break;
            // otherwise, add the payload to the buffer
            byte[] payload = this.window[idx].getPayload();
            for (byte b : payload) {
                this.buffer.add(b);
            }
        }
    }

}
