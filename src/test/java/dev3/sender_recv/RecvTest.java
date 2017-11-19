package dev3.sender_recv;

import assignment3.client.RUDP;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-17
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class RecvTest {


    public static void main(String[] args) throws IOException {
        RUDP rudp = new RUDP(8008, "localhost", 8007);
        rudp.send("hello world");
    }
}
