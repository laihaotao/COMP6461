package assignment3.client;

import assignment3.exception.HandShakingFailException;
import assignment3.observer.NoticeMsg;
import assignment3.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-15
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


// theory of setup the connection (three ways handshake)
// 1. randomly pick a initial sequence number and sendHandshakePacket a SYN segment;
// 2. wait for the connection-granted segment from the targeted host;
// 3. allocate buffers and variables for that connection, ack remote sequence number;

public class Connection extends Observer {

    private static final Logger logger = LoggerFactory.getLogger(Connection.class);

    private long    localSeqNum;
    private long    remoteSeqNum;
    private boolean hasConnected;

    private InetSocketAddress targetAddress;
    private ChannelThread     channelThread;

    public Connection(ChannelThread channelThread) {
        this.channelThread = channelThread;
        this.localSeqNum = (long) new Random().nextInt();
    }

    public void connect(InetSocketAddress targetAddress) throws HandShakingFailException {
        this.targetAddress = targetAddress;
        this.sendInitialSegment();
    }

    public void send(byte[] message) throws IOException {
        if (this.hasConnected) {
            // break the message into chunks
            Packet[] packets = makeChunks(message);
            // put all chunks into the SenderThread buffer
            this.channelThread.send(packets);
        }
    }

    private Packet[] makeChunks(byte[] message) {
        int      mLen      = message.length;
        int      packetAmt = (mLen / Packet.MAX_DATA) + 1;
        int      offset    = 0;
        Packet[] packets   = new Packet[packetAmt];
        for (int i = 0; i < packetAmt; i++) {
            byte[] tmp = new byte[Packet.MAX_DATA];
            int    len = ((mLen - offset) < Packet.MAX_DATA) ? (mLen - offset) : Packet.MAX_DATA;
            System.arraycopy(message, offset, tmp, 0, len);
            Packet p = new Packet.Builder()
                    .setType(Packet.DATA)
                    .setSequenceNumber(this.localSeqNum++)
                    .setPortNumber(this.targetAddress.getPort())
                    .setPeerAddress(this.targetAddress.getAddress())
                    .setPayload(tmp)
                    .create();
            packets[i] = p;
        }
        return packets;
    }

    private void sendInitialSegment() {
        this.localSeqNum = this.updateLocalSequenceNum();
        // create a initial handshake packet
        Packet packet = new Packet.Builder()
                .setType(Packet.SYN_1)
                .setSequenceNumber(this.localSeqNum)
                .setPortNumber(this.targetAddress.getPort())
                .setPeerAddress(this.targetAddress.getAddress())
                .setPayload("".getBytes())
                .create();
        // send handshake packet to the router
        this.channelThread.sendHandshakePacket(packet);
        logger.debug("Handshaking #1 SYN packet has already sent out");
    }

    private long updateLocalSequenceNum() {
        long seq = this.localSeqNum;
        this.localSeqNum++;
        return seq;
    }

    @Override
    protected void update(NoticeMsg msg, Packet recvPacket) {
        switch (msg) {
            case SYN_ACK:
                logger.debug("Handshaking #2 ACK_SYN packet has received");
                this.remoteSeqNum = recvPacket.getSequenceNumber();
                Packet p = new Packet.Builder()
                        .setType(Packet.SYN_2)
                        .setSequenceNumber(this.remoteSeqNum + 1)
                        .setPortNumber(this.targetAddress.getPort())
                        .setPeerAddress(this.targetAddress.getAddress())
                        .setPayload(null)
                        .create();
                this.channelThread.sendHandshakePacket(p);
                logger.debug("Handshaking #3 ACK_SYN packet has sent out");
                break;
            case SYN_ACK_ACK:
                this.hasConnected = true;
                logger.debug("Handshaking success, connection established");
                break;
        }
    }
}
