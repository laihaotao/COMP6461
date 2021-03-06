package assignment3.sr;

import assignment3.RUDP.Packet;
import assignment3.observer.NoticeMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-19
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ServerConnection extends Connection {

    private static final Logger logger = LoggerFactory.getLogger(ServerConnection.class);

    private long remoteSeqNum;
    private long localSeqNum;

    private SocketAddress     router;
    private ChannelThread     channelThread;
    private InetSocketAddress targetAddress;

    public ServerConnection(ChannelThread channelThread, SocketAddress routerAddr) {
        super();
        this.channelThread = channelThread;
        this.router = routerAddr;
//        this.localSeqNum   = (long) new Random().nextInt();
        this.localSeqNum = 8000;
    }

    @Override
    protected void update(NoticeMsg msg, Packet recvPacket) throws IOException {
        switch (msg) {
            case SYN:
                this.answerSYN(recvPacket);
                break;
            case SYN_ACK:
                this.answerSYNACK(recvPacket);
                break;
        }
    }

    private void answerSYN(Packet recvPacket) throws IOException {
        InetAddress addr = recvPacket.getPeerAddress();
//        this.targetAddress = new InetSocketAddress(addr, recvPacket.getPeerPort());
        this.targetAddress = new InetSocketAddress("localhost", 8098);

        logger.debug("Handshaking #1 SYN packet has received");
        this.remoteSeqNum = recvPacket.getSequenceNumber();
        Packet p = new Packet.Builder()
                .setType(Packet.SYN_2)
                .setSequenceNumber(this.localSeqNum)
                .setPortNumber(this.targetAddress.getPort())
                .setPeerAddress(this.targetAddress.getAddress())
                .setPayload("".getBytes())
                .create();

//        this.channelThread.getChannel().send(p.toBuffer(), this.router);
        this.channelThread.getChannel().send(p.toBuffer(), this.targetAddress);

        logger.debug("Handshaking #2 ACK_SYN packet has sent out");
    }

    private void answerSYNACK(Packet recvPacket) throws IOException {
        if (recvPacket.getSequenceNumber() == this.localSeqNum + 1) {
            logger.debug("Handshaking #3 SYN packet has received");
            Packet p = new Packet.Builder()
                    .setType(Packet.SYN_3)
                    .setSequenceNumber(this.remoteSeqNum + 1)
                    .setPortNumber(this.targetAddress.getPort())
                    .setPeerAddress(this.targetAddress.getAddress())
                    .setPayload("".getBytes())
                    .create();

//            this.channelThread.getChannel().send(p.toBuffer(), this.router);
            this.channelThread.getChannel().send(p.toBuffer(), this.targetAddress);
            this.connected = true;
            logger.debug("Handshaking #4 SYN_ACK_ACK packet has sent out");
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Packet[] makeChunks(byte[] message, boolean isAddEOD) {
        int counter = 0;
        for (; counter < message.length; counter++) {
            if (message[counter] == 0) break;
        }

        int      mLen      = counter;
        int      packetAmt = (mLen / Packet.MAX_DATA) + 1;
        int      offset    = 0;

        Packet[] packets;
        if (isAddEOD)
            packets = new Packet[packetAmt + 1];
        else
            packets = new Packet[packetAmt];

        for (int i = 0, pLen = packets.length; i < pLen; i++) {
            byte[] tmp = new byte[Packet.MAX_DATA];
            int    len = ((mLen - offset) < Packet.MAX_DATA) ? (mLen - offset) : Packet.MAX_DATA;
            System.arraycopy(message, offset, tmp, 0, len);
            int type;
            if (isAddEOD && i == pLen - 1) {
                type = Packet.EOD;
                tmp = "".getBytes();
            }
            else {
                type = Packet.DATA;
            }
            Packet p = new Packet.Builder()
                    .setType(type)
                    .setSequenceNumber(++this.localSeqNum)
                    .setPortNumber(this.targetAddress.getPort())
                    .setPeerAddress(this.targetAddress.getAddress())
                    .setPayload(tmp)
                    .create();
            packets[i] = p;
        }
        return packets;
    }

    public Packet generateEndOfDataPacket() {
        return new Packet.Builder()
                .setType(Packet.EOD)
                .setSequenceNumber(++this.localSeqNum)
                .setPortNumber(this.targetAddress.getPort())
                .setPeerAddress(this.targetAddress.getAddress())
                .setPayload("".getBytes())
                .create();
    }
}
