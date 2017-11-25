package assignment3.sr;

import assignment3.RUDP.Packet;
import assignment3.observer.NoticeMsg;
import assignment3.observer.Subject;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-18
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Timer extends Subject implements Runnable{

    private final long TIME_OUT = 1000;

    private Packet  packet;
    private boolean isAcked;

    public Timer(Packet packet) {
        this.packet   = packet;
        this.isAcked = false;
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

    public boolean isAcked() {
        return isAcked;
    }

    public void setAcked(boolean acked) {
        this.isAcked = acked;
    }

}
