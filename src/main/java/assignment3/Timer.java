package assignment3;

import assignment3.observer.Observer;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Timer implements Runnable {

    private TimerCallback   timerCallback;
    private long            localSeqNum;
    private Packet          packet;
    private SocketAddress   routerAddress;


    public Timer() {
    }

    public void registerCallback(TimerCallback timerCallback) {
        this.timerCallback = timerCallback;
    }

    @Override
    public void run() {
        boolean recvDataAck = false;
        // send the packet out and set a timer, and wait for timeout
        try (DatagramChannel channel = DatagramChannel.open()) {
            UDP.send(channel, packet.toBuffer(), routerAddress);
            recvDataAck = UDP.recvDataAck(channel, localSeqNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // call the listener to inform the window the resultHandler
        timerCallback.handler(recvDataAck, this.packet);
    }
}
