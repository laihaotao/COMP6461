package dev3.sender_recv;

import assignment3.server.ClientRUDP;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-17
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ClientTest {


    public static void main(String[] args) throws IOException {
        ClientRUDP clientRudp = new ClientRUDP(8008, "localhost", 8007);
        clientRudp.send("hello world");
    }
}
