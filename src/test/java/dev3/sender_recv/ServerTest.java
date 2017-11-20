package dev3.sender_recv;

import assignment3.RUDP.ServerRUDP;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-19
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ServerTest {

    public static void main(String[] args) {
        try {
            ServerRUDP server = new ServerRUDP(8007);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
