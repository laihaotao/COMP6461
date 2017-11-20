package assignment3.observer;

import assignment3.RUDP.Packet;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public abstract class Observer {

    protected abstract void update(NoticeMsg msg, Packet packet) throws IOException;
}
