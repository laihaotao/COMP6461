package assignment3.buffer;

import assignment3.Packet;
import assignment3.Window;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class SenderBuffer {

    private List<Packet> buffer;
    private Window       window;
    private long         localSeq;
    private long         remoteSeq;

    public SenderBuffer(long localSeq, long remoteSeq) {
        this.buffer    = new LinkedList<>();
        this.localSeq  = localSeq;
        this.remoteSeq = remoteSeq;
        this.window    = new Window(this);
    }

    public void add(Packet[] packets) {
        for (Packet p : packets) {
            int size = this.buffer.size();
            this.buffer.add(p);
            if (size < this.window.WINDOW_SIZE) {
                this.window.add(p);
            }
        }
    }

    public void remove() {
        this.buffer.remove(0);
    }

    public List<Packet> getFirstN(int n) {
        List<Packet> list = new LinkedList<>();
        int          size = this.buffer.size();
        int          j    = (n < size) ? n : size;
        for (int i = 0; i < j; i++) {
            list.add(this.buffer.get(i));
        }
        return list;
    }

}
