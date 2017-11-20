package assignment3.sr;

import assignment3.RUDP.Packet;
import assignment3.observer.NoticeMsg;
import assignment3.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    protected void update(NoticeMsg msg, Packet packet) {
        switch (msg) {
            case WIN_CHECK:
                if (this.timerMap.isEmpty()) {
                    this.basePacket = packet;
                    this.lastPacket = packet;
                }
                // every time when we are adding packet into the
                // buffer, the following codes will be invoked
                if (this.timerMap.size() < this.WIN_SIZE) {
                    // send the packet and move the lastPacket pointer
                    this.send(packet);
                    this.lastPacket = packet;
                }
                break;
            case ACK:
                if (isAckBasePacket(packet)) {
                    this.terminateTimer(packet.getSequenceNumber());
                    this.slideWindow();
                    logger.debug("Current basePacket # {} was acked, slide down window",
                            packet.getSequenceNumber());
                } else {
                    this.terminateTimer(packet.getSequenceNumber());
                    logger.debug("Packet # {} was acked, but not basePacket",
                            packet.getSequenceNumber());
                }
                break;
            case TIME_OUT:
                // get current packet's timer, check the status
                Timer timer = this.timerMap.get(packet.getSequenceNumber());
                if (!timer.isHasAcked()) {
                    // if the packet hasn't receive an
                    // ack we have to retransmit it
                    int nextSending = this.queue.indexOf(this.lastPacket) + 1;
                    this.thread.send(nextSending, packet);
                    logger.info("Time out happen packet # {}", packet.getSequenceNumber());
                }
        }
    }

    private boolean isAckBasePacket(Packet ackPacket) {
        if (ackPacket != null) {
            long ackPckSeqNum = ackPacket.getSequenceNumber();
            return ackPckSeqNum == this.basePacket.getSequenceNumber();
        }
        return false;
    }

    private void terminateTimer(long ackPckSeqNum) {
        // find that packet waiting for the ack with the ackNum, if it exists terminate its timer
        if (this.timerMap.containsKey(ackPckSeqNum)) {
            this.timerMap.get(ackPckSeqNum).setHasAcked(true);
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
        // check how many ack received counting from the basePacket
        int n = this.getNumberOfAckPacket();

        // remove the first min element, since they have been acked
        for (int i = 0; i < n; i++) {
            this.queue.remove(0);
        }
        // move the basePacket pointer to the appropriate place
        // and update the lastPacket pointer's position
        if (!this.queue.isEmpty()) {
            this.basePacket = this.queue.get(0);
            int min = Math.min(this.queue.size(), this.WIN_SIZE);
            Packet prev     = this.lastPacket;
            this.lastPacket = this.queue.get(min);
            // check everything inside current window and send
            // that packets which are ready to be sent
            this.checkAndSend(prev, min);
        } else {
            this.basePacket = null;
            this.lastPacket = null;
        }
    }

    private int getNumberOfAckPacket() {
        int n = 0;
        for (int i = 0; i < this.timerMap.size(); i++){
            long seqNum = this.queue.get(0).getSequenceNumber();
            if (this.timerMap.get(seqNum).isHasAcked()) n++;
            else break;
        }
        return n;
    }

    private void checkAndSend(Packet prev, int min) {
        int idx = this.queue.indexOf(prev) + 1;
        for (int i = 0; i < min; i++) {
            this.send(this.queue.get(idx + i));
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
