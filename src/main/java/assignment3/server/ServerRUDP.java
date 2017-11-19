package assignment3.server;

import assignment3.Connection;
import assignment3.ServerConnection;
import assignment3.client.ChannelThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-19
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ServerRUDP {

    private DatagramChannel channel;
    private SocketAddress   localAddr;
    private SocketAddress   routerAddr;
    private Connection      connection;

    public ServerRUDP(int localPort) {
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
            ChannelThread channelThread = new ChannelThread(this.channel, this.routerAddr);
            Thread        recvThread    = new Thread(channelThread);
            this.connection             = new ServerConnection(channelThread);
            channelThread.attach(this.connection);
            recvThread.start();

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
