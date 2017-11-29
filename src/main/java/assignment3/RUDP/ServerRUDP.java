package assignment3.RUDP;

import assignment2_2.HttpResponse;
import assignment3.sr.ChannelThread;
import assignment3.sr.Connection;
import assignment3.sr.RecvBuffer;
import assignment3.sr.ServerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-19
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ServerRUDP {

    private static final Logger logger = LoggerFactory.getLogger(ServerRUDP.class);


    private DatagramChannel channel;
    private Connection      connection;
    private ChannelThread   thread;
    private RecvBuffer      recvBuffer;
    private SocketAddress   localAddr;
    private SocketAddress   routerAddr;

    public ServerRUDP(int localPort) throws IOException {
        this.localAddr  = new InetSocketAddress(localPort);
        this.routerAddr = new InetSocketAddress("localhost", 3000);
        this.init();
    }

    private void init() throws IOException {

        // open the datagram channel, keep it and
        // using it for data sending and receiving
        this.channel    = DatagramChannel.open();
        this.channel.bind(this.localAddr);
        logger.debug("Server is listening on: " + this.localAddr);

        // start a thread to listen for the incoming data
        this.thread       = new ChannelThread(this.channel, this.routerAddr);
        Thread recvThread = new Thread(this.thread);
        this.connection   = new ServerConnection(this.thread, this.routerAddr);
        this.thread.attach(this.connection);
        this.thread.bind(this.connection);

        // allocate the data received buffer
        this.recvBuffer = new RecvBuffer(this.thread, this.routerAddr, this.connection);
        this.thread.attach(this.recvBuffer);
        recvThread.start();
    }

    public void send(String message) throws IOException {
        this.thread.send(message.getBytes("utf-8"));
    }

    public int receive(ByteBuffer result) {
        return this.recvBuffer.receive(result);
    }

    public void sendResponse(HttpResponse response) throws IOException {
        if (response.strBody != null) {
            this.thread.send((response.toString() + response.strBody).getBytes("utf-8"));
        }
        else if (response.getFileBody() != null) {
            List<Packet> list = new ArrayList<>();
            byte[] out_buf;
            out_buf = response.toString().getBytes("utf-8");
            list.addAll(Arrays.asList(this.connection.makeChunks(out_buf, false)));
            while ((out_buf = response.getBody()) != null) {
                list.addAll(Arrays.asList(this.connection.makeChunks(out_buf, false)));
            }
            list.add(((ServerConnection)this.connection).generateEndOfDataPacket());
            for (Packet p : list) {
                this.thread.send(p);
            }
        }

    }
}
