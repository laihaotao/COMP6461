package assignment3;

import assignment3.client.ChannelThread;
import assignment3.observer.NoticeMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Random;

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

    private InetSocketAddress targetAddress;
    private ChannelThread     channelThread;

    public ServerConnection(ChannelThread channelThread) {
        super();
        this.channelThread = channelThread;
        this.localSeqNum   = (long) new Random().nextInt();
    }

    @Override
    protected void update(NoticeMsg msg, Packet recvPacket) {
        switch (msg) {
            case SYN:
                this.answerSYN(recvPacket);
                break;
            case SYN_ACK:
                this.answerSYNACK(recvPacket);
                break;
        }
    }

    private void answerSYN(Packet recvPacket) {
        InetAddress addr = recvPacket.getPeerAddress();
        this.targetAddress = new InetSocketAddress(addr, recvPacket.getPeerPort());
        logger.debug("Handshaking #1 SYN packet has received");
        this.remoteSeqNum = recvPacket.getSequenceNumber();
        Packet p = new Packet.Builder()
                .setType(Packet.SYN_2)
                .setSequenceNumber(this.localSeqNum)
                .setPortNumber(this.targetAddress.getPort())
                .setPeerAddress(this.targetAddress.getAddress())
                .setPayload("".getBytes())
                .create();
        this.channelThread.sendHandshakePacket(p);
        logger.debug("Handshaking #2 ACK_SYN packet has sent out");
    }

    private void answerSYNACK(Packet recvPacket) {
        logger.debug("Handshaking #3 SYN packet has received");
        this.remoteSeqNum = recvPacket.getSequenceNumber();
        Packet p = new Packet.Builder()
                .setType(Packet.SYN_3)
                .setSequenceNumber(this.remoteSeqNum + 1)
                .setPortNumber(this.targetAddress.getPort())
                .setPeerAddress(this.targetAddress.getAddress())
                .setPayload("".getBytes())
                .create();
        this.channelThread.sendHandshakePacket(p);
        logger.debug("Handshaking #4 SYN_ACK_ACK packet has sent out");
    }
}
