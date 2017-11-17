package dev3.example2;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-17
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class TestUDP {

    @Test
    public void test() throws IOException {
        SocketAddress addr = new InetSocketAddress("localhost", 8007);
        UDPClient c1 = new UDPClient(addr);
        UDPClient c2 = new UDPClient(addr);
        c1.send("hello world 1");
        c2.send("hello world 2");

    }
}
