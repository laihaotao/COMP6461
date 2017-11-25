package assignment3.sr;

import assignment3.RUDP.Packet;
import assignment3.exception.HandShakingFailException;
import assignment3.observer.NoticeMsg;
import assignment3.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

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

    private long          localSeqNum;
    private long          remoteSeqNum;
    private long          handshakeNum;
    private boolean       connected;
    private SocketAddress router;

    private InetSocketAddress targetAddress;
    private ChannelThread     channelThread;

    public Connection() { }

    public Connection(ChannelThread channelThread, SocketAddress routerAddr) {
        this.channelThread = channelThread;
        this.router = routerAddr;
//        this.localSeqNum = (long) new Random().nextInt();
        this.localSeqNum = 1000;
    }

    public void connect(InetSocketAddress targetAddress)
            throws HandShakingFailException, IOException {
        this.targetAddress = targetAddress;
        this.sendInitialSegment();
    }

    public Packet[] makeChunks(byte[] message) {
        int      mLen      = message.length;
        int      packetAmt = (mLen / Packet.MAX_DATA) + 1;
        int      offset    = 0;
        Packet[] packets   = new Packet[packetAmt + 1];
        for (int i = 0, pLen = packets.length; i < pLen; i++) {
            byte[] tmp = new byte[Packet.MAX_DATA];
            int    len = ((mLen - offset) < Packet.MAX_DATA) ? (mLen - offset) : Packet.MAX_DATA;
            System.arraycopy(message, offset, tmp, 0, len);
            int type;
            if (i == pLen - 1) {
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

    private void sendInitialSegment() throws IOException {
        this.handshakeNum = this.localSeqNum;
        // create a initial handshake packet
        Packet packet = new Packet.Builder()
                .setType(Packet.SYN_1)
                .setSequenceNumber(this.localSeqNum)
                .setPortNumber(this.targetAddress.getPort())
                .setPeerAddress(this.targetAddress.getAddress())
                .setPayload("".getBytes())
                .create();
//
//        // send handshake packet to the router
//        this.channelThread.getChannel().send(packet.toBuffer(), router);

        // let's assume all the handshake packet will not be dropped by the router
        this.channelThread.getChannel().send(packet.toBuffer(), this.targetAddress);

        logger.debug("Handshaking #1 SYN packet has already sent out");
    }

    @Override
    protected void update(NoticeMsg msg, Packet recvPacket) throws IOException {
        switch (msg) {
            case SYN_ACK:
                logger.debug("Handshaking #2 ACK_SYN packet has received");
                this.remoteSeqNum = recvPacket.getSequenceNumber();
                Packet p = new Packet.Builder()
                        .setType(Packet.SYN_2)
                        .setSequenceNumber((this.remoteSeqNum + 1))
                        .setPortNumber(this.targetAddress.getPort())
                        .setPeerAddress(this.targetAddress.getAddress())
                        .setPayload("".getBytes())
                        .create();

//                this.channelThread.getChannel().send(p.toBuffer(), this.router);
                this.channelThread.getChannel().send(p.toBuffer(), this.targetAddress);

                logger.debug("Handshaking #3 ACK_SYN packet has sent out");
                break;
            case SYN_ACK_ACK:
                if (recvPacket.getSequenceNumber() == this.handshakeNum + 1) {
                    this.connected = true;
                    logger.info("Handshaking success, connection established");
                }
                break;
        }
    }

    public synchronized boolean isConnected() {
        return connected;
    }
}
