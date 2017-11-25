package assignment3.sr;

import assignment3.RUDP.Packet;
import assignment3.observer.NoticeMsg;
import assignment3.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-18
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Window extends Observer {

    private static final Logger logger = LoggerFactory.getLogger(Window.class);

    private final int WIN_SIZE = 4;

    private ChannelThread    thread;
    private List<Packet>     queue;
    private Map<Long, Timer> timerMap;

    private int    hasSth2Send;
    private Packet basePacket;
    private Packet lastPacket;

    public Window(List<Packet> queue, ChannelThread channelThread) {
        this.queue    = queue;
        this.thread   = channelThread;
        this.timerMap = new HashMap<>();
    }

    @Override
    protected void update(NoticeMsg msg, Packet packet) throws IOException {
        switch (msg) {
            case WIN_CHECK:
                // every time when we are adding packet into the
                // buffer, the following codes will be invoked
                if (this.timerMap.isEmpty()) {
                    this.basePacket = packet;
                    this.lastPacket = packet;
                    this.send(packet);
                } else if (this.timerMap.size() < this.WIN_SIZE) {
                    // send the packet and move the lastPacket pointer
                    this.send(packet);
                    this.lastPacket = packet;
                }
                break;
            case ACK:
                if (packet != null && this.basePacket != null
                    && packet.getSequenceNumber() == basePacket.getSequenceNumber()) {
                    this.terminateTimer(packet.getSequenceNumber());
                    this.slideWindow();
                    logger.debug("Current basePacket # {} was acked, slide down window",
                            packet.getSequenceNumber());
                } else if (packet != null){
                    this.terminateTimer(packet.getSequenceNumber());
                    logger.debug("Packet # {} was acked, but not basePacket",
                            packet.getSequenceNumber());
                }
                break;
            case TIME_OUT:
                // get current packet's timer, check the status
                long key = packet.getSequenceNumber();
                Timer timer = this.timerMap.get(key);
                if (!timer.isAcked()) {
                    // if the real timeout happen
                    this.thread.getChannel().send(packet.toBuffer(), this.thread.getRouter());
                    new Thread(timer).start();
                    logger.info("Time out happen packet # {}", key);
                } else {
                    // no real timeout, it is time to remove this timer
                    this.timerMap.remove(key);
                }
        }
    }

    private void terminateTimer(long ackPckSeqNum) {
        if (this.timerMap.containsKey(ackPckSeqNum)) {
            Timer timer = this.timerMap.get(ackPckSeqNum);
            timer.setAcked(true);
        }
    }

    public void send(Packet packet) {
        // send a packet
        int i = this.getHasSth2Send() + 1;
        this.setHasSth2Send(i);
        // associate a timer with that packet
        Timer timer = new Timer(packet);
        timer.attach(this);
        this.timerMap.put(packet.getSequenceNumber(), timer);
    }

    private void slideWindow() {
        int offset = this.getAlreadyAckAmt();
        for (int i = 0; i < offset; i++) {
            if (!this.queue.isEmpty())
                this.queue.remove(0);
            else
                break;
        }

        if (this.queue.isEmpty()) {
            this.basePacket = null;
            this.lastPacket = null;
        } else {
            this.basePacket = this.queue.get(0);
            int lastIdx = Math.min(this.WIN_SIZE - 1, this.queue.size() - 1);
            this.lastPacket = this.queue.get(lastIdx);
            this.checkAndSend();
        }
    }

    private int getAlreadyAckAmt() {
        int counter = 0;
        Packet cur = this.basePacket;
        while (counter < this.WIN_SIZE) {
            if (this.timerMap.containsKey(cur.getSequenceNumber())
                && this.timerMap.get(cur.getSequenceNumber()).isAcked()) {
                counter++;
            } else break;
        }
        return counter;
    }

    private void checkAndSend() {
        Iterator<Packet> it = this.queue.listIterator();
        Packet cur;
        while (it.hasNext()) {
            cur = it.next();
            long key = cur.getSequenceNumber();
            if (!this.timerMap.containsKey(key)) {
                this.send(cur);
            }
            if (cur == this.lastPacket) break;
        }
    }

    public synchronized int getHasSth2Send() {
        return hasSth2Send;
    }

    public synchronized void setHasSth2Send(int hasSth2Send) {
        this.hasSth2Send = hasSth2Send;
    }

    public Map<Long, Timer> getTimerMap() {
        return timerMap;
    }
}
