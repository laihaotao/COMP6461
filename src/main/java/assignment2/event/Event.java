package assignment2.event;

import java.nio.channels.SocketChannel;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Event {

    public String        rawData;
    public SocketChannel from;

    public Event(String rawData, SocketChannel from) {
        this.rawData = rawData;
        this.from    = from;
    }

}
