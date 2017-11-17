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

    public Connection() {
    }

    public Connection(InetSocketAddress targetAddress) {
        this.connect(targetAddress);
    }

    public void connect(InetSocketAddress targetAddress) throws HandShakingFailException {
        this.targetAddress = targetAddress;
        this.sendInitialSegment();
        this.buffer = new SenderBuffer(this.localSeqNum, this.remoteSeqNum);
    }

    public void send(byte[] message) throws IOException {
        // break the message into chunks
        Packet[] packets = makeChunks(message);
        // put all chunks into the sender buffer
        this.buffer.add(packets);
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

    private void sendInitialSegment() throws HandShakingFailException {
        int  repeat = 5;
        long remoteSeq;
        try (DatagramChannel channel = DatagramChannel.open()) {
            do {
                this.localSeqNum = this.updateLocalSequenceNum();
                // create a initial handshake packet
                Packet packet = new Packet.Builder()
                        .setType(Packet.SYN_1)
                        .setSequenceNumber(this.localSeqNum)
                        .setPortNumber(this.targetAddress.getPort())
                        .setPeerAddress(this.targetAddress.getAddress())
                        .setPayload(null)
                        .create();
                // send to packet to the router
                UDP.send(channel, packet.toBuffer(), this.routerAddress);

                // try to wait for the acknowledge from the remote host
                // if the remoteSeq = -1, means cannot receive the SYN need to resend
                remoteSeq = UDP.recvSynAck(channel);
                repeat--;
            } while (remoteSeq == -1 && repeat >= 0);

            if (repeat < 0) {
                // it means the initial packet cannot be acknowledged correctly
                throw new HandShakingFailException();
            } else {
                // otherwise, SYN received correctly then send the third packet
                this.remoteSeqNum = remoteSeq;
                Packet packet = new Packet.Builder()
                        .setType(Packet.SYN_2)
                        .setSequenceNumber(this.remoteSeqNum + 1)
                        .setPortNumber(this.targetAddress.getPort())
                        .setPeerAddress(this.targetAddress.getAddress())
                        .setPayload(null)
                        .create();
                UDP.send(channel, packet.toBuffer(), this.routerAddress);
                if (!UDP.recvSynAckAck(channel, this.localSeqNum + 1)) {
                    throw new HandShakingFailException();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long updateLocalSequenceNum() {
        // todo: need to design the sequence number selection algorithm
        return 1L;
    }

}
