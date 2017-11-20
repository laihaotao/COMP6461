package assignment3.RUDP;

import assignment3.sr.ChannelThread;
import assignment3.sr.Connection;
import assignment3.sr.RecvBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ClientRUDP {

    private static final Logger logger = LoggerFactory.getLogger(ClientRUDP.class);

    private DatagramChannel channel;
    private Connection      connection;
    private ChannelThread   thread;
    private RecvBuffer      recvBuffer;
    private SocketAddress   localAddr;
    private SocketAddress   routerAddr;

    public ClientRUDP(int localPort, String remoteAddr, int remotePort) throws IOException {
        this.localAddr  = new InetSocketAddress(localPort);
        this.routerAddr = new InetSocketAddress("localhost", 3000);
        this.init(remoteAddr, remotePort);
    }

    private void init(String remoteAddr, int remotePort) throws IOException {

        // open the datagram channel, keep it and
        // using it for data sending and receiving
        this.channel = DatagramChannel.open();
        logger.debug("Client is binding to: " + this.localAddr);
        this.channel.bind(this.localAddr);

        // start a thread to listen for the incoming data
        this.thread = new ChannelThread(this.channel, this.routerAddr);
        this.connection = new Connection(thread, this.routerAddr);
        thread.attach(this.connection);
        thread.bind(this.connection);

        // allocate the data received buffer
        this.recvBuffer = new RecvBuffer(this.thread, this.routerAddr);
        this.thread.attach(this.recvBuffer);

        // build the connection between sender and receiver
        this.connection.connect(new InetSocketAddress(remoteAddr, remotePort));

        new Thread(this.thread).start();
    }

    public void send(String message) throws IOException {
        this.thread.send(message.getBytes("utf-8"));
    }

    public int receive(ByteBuffer result) {
        return this.recvBuffer.receive(result);
    }

    // just for testing purpose
    public void sendMultiplePacket(int amount) {
        long seqNum = 1001;
        InetSocketAddress addr = new InetSocketAddress("localhost", 8007);
        for (int i = 0; i<amount;i++) {
            Packet p = new Packet.Builder()
                    .setType(Packet.DATA)
                    .setSequenceNumber(seqNum)
                    .setPortNumber(addr.getPort())
                    .setPeerAddress(addr.getAddress())
                    .setPayload(("hello world " + seqNum).getBytes())
                    .create();
            this.thread.send(p);
            seqNum++;
        }
    }
}
