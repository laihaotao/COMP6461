package assignment3.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class RUDP {

    private int    localPort;
    private String remoteAddr;
    private int    remotePort;

    private DatagramChannel channel;
    private SocketAddress   localAddr;
    private SocketAddress   routerAddr;
    private Connection      connection;

    public RUDP(int localPort, String remoteAddr, int remotePort) {
        this.localPort  = localPort;
        this.remoteAddr = remoteAddr;
        this.remotePort = remotePort;
        this.localAddr  = new InetSocketAddress(localPort);
        this.routerAddr = new InetSocketAddress("localhost", 3000);
        this.init();
    }

    private void init() {
        try {
            // open the datagram channel, keep it and
            // using it for data sending and receiving
            this.channel = DatagramChannel.open();
            this.channel.bind(localAddr);

            // start a thread to listen for the incoming data
            ChannelThread senderThread = new ChannelThread(this.channel, this.routerAddr);
            Thread recvThread          = new Thread(senderThread);
            this.connection            = new Connection(senderThread);
            senderThread.attach(this.connection);
            recvThread.start();

            // build the connection between sender and receiver
            this.connection.connect(new InetSocketAddress(this.remoteAddr, this.remotePort));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) throws IOException {
        // call the connection's sendHandshakePacket method
        this.connection.send(message.getBytes("utf-8"));
    }

    public void receive() {

    }
}
