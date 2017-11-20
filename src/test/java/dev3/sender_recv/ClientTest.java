package dev3.sender_recv;

import assignment3.RUDP.ClientRUDP;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-17
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ClientTest {


    public static void main(String[] args) {
        try {

//            new ClientTest().singlePacket();

            new ClientTest().multiplePacket();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void multiplePacket() throws IOException {
        ClientRUDP clientRudp = new ClientRUDP(8098, "localhost", 8007);
        clientRudp.sendMultiplePacket(10);
    }

    public void singlePacket() throws IOException {
        ClientRUDP clientRudp = new ClientRUDP(8098, "localhost", 8007);

        clientRudp.send("hello world");
    }
}
