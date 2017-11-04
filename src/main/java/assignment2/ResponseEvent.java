package assignment2;

import java.nio.channels.SocketChannel;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-21
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ResponseEvent extends Event{

    public ResponseEvent(String rawData, SocketChannel from) {
        super(rawData, from);
    }
}
