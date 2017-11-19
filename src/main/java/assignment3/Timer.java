package assignment3;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-18
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Timer {

    private long    seqNum;
    private Packet  packet;
    private boolean hasAcked;

    public void finish() {

    }

    public long getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(long seqNum) {
        this.seqNum = seqNum;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public boolean isHasAcked() {
        return hasAcked;
    }

    public void setHasAcked(boolean hasAcked) {
        this.hasAcked = hasAcked;
    }
}
