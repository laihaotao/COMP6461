package dev3.sender_recv;

import assignment3.RUDP.ServerRUDP;

import java.io.IOException;
import java.nio.ByteBuffer;

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
            int len;
            ByteBuffer buf = ByteBuffer.allocate(1024);
            StringBuilder builder = new StringBuilder();

            while ((len = server.receive(buf)) != -1) {
                byte[] raw = buf.array();
                byte[] bytes = new byte[len];
                System.arraycopy(raw, 0, bytes, 0, len);
                String content = new String(bytes);
                builder.append(content);
            }

            System.out.println(builder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
