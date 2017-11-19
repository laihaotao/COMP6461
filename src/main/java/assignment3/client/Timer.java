package assignment3.client;

import assignment3.observer.NoticeMsg;
import assignment3.observer.Subject;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-18
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Timer extends Subject implements Runnable{

    private final long TIME_OUT = 500;

    private Packet  packet;
    private boolean hasAcked;

    public Timer(Packet packet) {
        this.packet   = packet;
        this.hasAcked = false;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(TIME_OUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // the timer wait for the interval time and time out it
        this.notifyObservers(NoticeMsg.TIME_OUT, packet);
    }

    public boolean isHasAcked() {
        return hasAcked;
    }

    public void setHasAcked(boolean hasAcked) {
        this.hasAcked = hasAcked;
    }

}
