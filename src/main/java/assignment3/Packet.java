package assignment3;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Packet {

    private final int BYTE_SIZE_2    = 2;
    private final int BYTE_SIZE_4    = 4;
    private final int BYTE_SIZE_1013 = 1013;

    private byte   type;
    private byte[] sequenceNum;
    private byte[] peerAddr;
    private byte[] peerPort;
    private byte[] payload;

    public Packet() {
        this.sequenceNum = new byte[BYTE_SIZE_4];
        this.peerAddr    = new byte[BYTE_SIZE_4];
        this.peerPort    = new byte[BYTE_SIZE_2];
        this.payload     = new byte[BYTE_SIZE_1013];
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte[] getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(byte[] sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public byte[] getPeerAddr() {
        return peerAddr;
    }

    public void setPeerAddr(byte[] peerAddr) {
        this.peerAddr = peerAddr;
    }

    public byte[] getPeerPort() {
        return peerPort;
    }

    public void setPeerPort(byte[] peerPort) {
        this.peerPort = peerPort;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
