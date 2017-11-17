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


public class SenderBuffer implements Runnable{

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

    @Override
    public void run() {
        window.init(this.buffer);
        window.send();
    }

    public void add(Packet[] packets) {
        Collections.addAll(this.buffer, packets);
    }

    public Packet[] getFirstN(int n) {
        Packet[] packets = new Packet[n];
        for (int i = 0; i < n; i++) {
            packets[i] = this.buffer.remove(i);
        }
        return packets;
    }

}
