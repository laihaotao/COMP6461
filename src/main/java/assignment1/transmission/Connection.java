package assignment1.transmission;

import assignment1.common.ParamHolder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Connection {

    public Connection(ParamHolder holder) throws IOException {
//        buildServerSocketAddress();
        try (SocketChannel socket = SocketChannel.open()) {
//            socket.connect(endpoint);
        }
    }

    public void buildServerSocketAddress(String host, int port) {
        SocketAddress server = new InetSocketAddress(host, port);
    }


}
