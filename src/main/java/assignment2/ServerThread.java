package assignment2;

import assignment2.common.ParamHolder;
import assignment2.event.Event;
import assignment2.event.RequestEvent;
import assignment2.event.ResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ServerThread implements Runnable {

    private final Logger logger   = LoggerFactory.getLogger(ServerThread.class);
    private final int    BUF_SIZE = 8192;

    private EventManager                   eventManager;
    private InetSocketAddress              hostAddress;
    private ServerSocketChannel            serverChannel;
    private Selector                       selector;
    private ByteBuffer                     buffer;
    private ParamHolder                    holder;
    private HashMap<SocketChannel, ResponseEvent> pendingData;

    public ServerThread(EventManager eventManager, ParamHolder holder) throws IOException {
        this.eventManager = eventManager;
        this.buffer       = ByteBuffer.allocate(BUF_SIZE);
        this.hostAddress  = new InetSocketAddress(holder.portNumber);
        this.selector     = this.initSelector();
        this.pendingData  = new HashMap<>();
        this.holder       = holder;
    }

    @Override
    public void run() {
        while (true) {

            while (!this.eventManager.isResQueueEmpty()) {
                ResponseEvent event = (ResponseEvent) this.eventManager.deResEventQueue();
                SelectionKey  key   = event.from.keyFor(this.selector);
                key.interestOps(SelectionKey.OP_WRITE);
                this.pendingData.put(event.from, event);
            }

            try {

                // Wait for an event one of the registered channels
                this.selector.select(1000);

                // Iterate over the set of keys for which events are available
                Iterator selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {continue;}

                    // Check what event is available and deal with it
                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    } else if (key.isWritable()) {
                        this.write(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Selector initSelector() throws IOException {
        // Create a new selector
        Selector socketSelector = SelectorProvider.provider().openSelector();

        // Create a new non-blocking server socket channel
        this.serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // Bind the server socket to the specified address and port
        serverChannel.socket().bind(hostAddress);

        logger.trace("Server start ...");
        logger.info("Create a new server at: {}", serverChannel.getLocalAddress());

        // Register the server socket channel, indicating an interest in
        // accepting new connections
        serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

        return socketSelector;
    }

    private void accept(SelectionKey key) throws IOException {
        // For an accept to be pending the channel must be a server socket channel.
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);

        logger.info("A new client comes: {}", clientChannel.getRemoteAddress());

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        clientChannel.register(this.selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        // Clear out our read buffer so it's ready for new data
        this.buffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = client.read(this.buffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            client.close();
            return;
        }

        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            key.channel().close();
            key.cancel();
            return;
        }

        addEvent2ReqQueue(client, numRead);
    }

    private void addEvent2ReqQueue(SocketChannel client, int numRead) {
        String rawData = new String(this.buffer.array(), 0, numRead);
        Event  event   = new RequestEvent(rawData, client, this.holder);
        this.eventManager.enReqEventQueue(event);
        this.eventManager.reqQueueNotify();
        logger.trace("Receive data from client: \n======\n{}\n======\n", rawData);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ResponseEvent event = pendingData.remove(channel);
        ByteBuffer buf = ByteBuffer.wrap(
                event.getResponse().toString().getBytes(Charset.forName("utf-8"))
        );
        channel.write(buf);
        if (event.getResponse().strBody != null) {
            buf.clear();
            buf = ByteBuffer.wrap(event.getResponse().strBody.getBytes("utf-8"));
            channel.write(buf);
        }
        if (event.getResponse().getFileBody() != null) {
            buf.clear();
            byte[] out_buf;
            while ((out_buf = event.getResponse().getBody()) != null) {
                buf = ByteBuffer.wrap(out_buf);
                channel.write(buf);
            }
        }
        key.interestOps(SelectionKey.OP_READ);
        channel.close();
    }
}
