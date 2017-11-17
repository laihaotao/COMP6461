package assignment3;

import assignment3.buffer.SenderBuffer;

import java.util.Collections;
import java.util.List;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Window {

    public final static int READY    = 100;
    public final static int RECEIVED = 200;

    public final int WINDOW_SIZE = 4;

    private SenderBuffer buffer;
    private List<Packet> window;
    private int[]        status;
    private Packet       basePacket;

    public Window(SenderBuffer buffer) {
        this.buffer = buffer;
        for (int i = 0; i < this.WINDOW_SIZE; i++) {
            this.status[i] = Window.READY;
        }
    }

    public void add(Packet p) {
        this.window.add(p);
        this.startTimerAndSendPacket(p);
    }
//
//    public void init(List<Packet> buffer) {
//        for (int i = 0; i < this.WINDOW_SIZE; i++) {
//            if (!buffer.isEmpty()) {
//                // always remove the first element from the sender buffer list
//                this.window.add(buffer.remove(0));
//            } else break;
//        }
//    }

    private void startTimerAndSendPacket(Packet p) {
        Timer timer = new Timer(p);
        Thread timerThread = new Thread(timer);

        TimerCallback callback = (recvDataAck, packet) -> {
            // if the timer thread timeout (cannot receive the ack for this packet),
            // we have to retransmit it again
            if (!recvDataAck) {
                timerThread.start();
            }
            // otherwise, check we can modify the status of that
            // packet and check whether slide down the window or not
            else {
                int seqNum = (int) packet.getSequenceNumber();
                status[seqNum] = Window.RECEIVED;
                if (this.basePacket == packet) {
                    this.slideWindow();
                }
            }
        };
        timer.registerCallback(callback);

        timerThread.start();
    }

    private void slideWindow() {
        // remove the packets which have bee acked by the receiver
        int n = 0;
        for (; n < this.WINDOW_SIZE; n++) {
            if (this.status[n] == Window.RECEIVED) {
                this.window.remove(0);
                this.buffer.remove();
            }
            else break;
        }
        // how many packets we remove means how many we can add
        if (n > 0) {
            List<Packet> tmp = this.buffer.getFirstN(n);
            for (Packet p : tmp) {
                this.startTimerAndSendPacket(p);
            }
        }
    }

}
