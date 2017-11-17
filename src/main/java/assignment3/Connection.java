package assignment3;

import assignment3.buffer.SenderBuffer;
import assignment3.exception.HandShakingFailException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-15
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


// theory of setup the connection (three ways handshake)
// 1. randomly pick a initial sequence number and send a SYN segment;
// 2. wait for the connection-granted segment from the targeted host;
// 3. allocate buffers and variables for that connection, ack remote sequence number;

public class Connection {

    private long    localSeqNum;
    private long    remoteSeqNum;
    private boolean hasHandShake;

    private SocketAddress     routerAddress;
    private InetSocketAddress targetAddress;
    private SenderBuffer      buffer;

    public Connection(InetSocketAddress targetAddress) throws IOException {
        this.targetAddress = targetAddress;
    }

    public void send(byte[] message) throws IOException, HandShakingFailException {
        // perform the handshake
        if (!hasHandShake) {
            this.sendInitialSegment();
            this.buffer = new SenderBuffer(this.localSeqNum, this.remoteSeqNum);
        }
        // break the message into chunks
        Packet[] packets = makeChunks(message);
        // put all chunks into the sender buffer
        this.buffer.add(packets);
        // let the buffer to send data using selective repeat protocol
        Thread sendThread = new Thread(this.buffer);
        if (!hasHandShake && !sendThread.isAlive()) {
            // if already handshake and the sending thread has
            // already end then start a new sending thread
            sendThread.start();
            hasHandShake = true;
        }
    }

    private Packet[] makeChunks(byte[] message) {
        int      mLen    = message.length;
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

    private void sendInitialSegment() throws IOException, HandShakingFailException {
        int  repeat = 5;
        long remoteSeq;
        try (DatagramChannel channel = DatagramChannel.open()) {
            do {
                this.localSeqNum = this.updateLocalSequenceNum();
                // create a initial handshake packet
                Packet packet = new Packet.Builder()
                        .setType(Packet.SYN)
                        .setSequenceNumber(this.localSeqNum)
                        .setPortNumber(this.targetAddress.getPort())
                        .setPeerAddress(this.targetAddress.getAddress())
                        .setPayload(null)
                        .create();
                // send to packet to the router
                UDP.send(channel, packet.toBuffer(), this.routerAddress);

                // try to wait for the acknowledge from the remote host
                // if the remoteSeq = -1, means cannot receive the SYN_ACK need to resend
                remoteSeq = UDP.recvSynAck(channel);
                repeat--;
            } while (remoteSeq == -1 && repeat >= 0);
        }

        if (repeat < 0) {
            // it means the initial packet cannot be acknowledged correctly
            throw new HandShakingFailException();
        }
        this.remoteSeqNum = remoteSeq;
    }

    private long updateLocalSequenceNum() {
        return 1L;
    }

}
