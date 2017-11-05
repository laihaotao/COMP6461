package assignment2;

import assignment2.event.Event;

import java.util.LinkedList;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class EventManager {

    private final LinkedList<Event> reqEvenQueue;
    private final LinkedList<Event> resEvenQueue;

    public EventManager() {
        this.reqEvenQueue = new LinkedList<>();
        this.resEvenQueue = new LinkedList<>();
    }

    public synchronized void enReqEventQueue(Event event) {
        this.reqEvenQueue.addLast(event);
    }

    public synchronized void enResEventQueue(Event event) {
        this.resEvenQueue.add(event);
//        synchronized (this.resEvenQueue) {
//            this.resEvenQueue.notifyAll();
//        }
    }

    public synchronized Event deReqEventQueue() {
        return this.reqEvenQueue.removeFirst();
    }

    public synchronized Event deResEventQueue() {
        return this.resEvenQueue.removeFirst();
    }

    public boolean isReqQueueEmpty() {
        return this.reqEvenQueue.isEmpty();
    }

    public boolean isResQueueEmpty() {
        return this.resEvenQueue.isEmpty();
    }

    public void reqQueueWait() throws InterruptedException {
        synchronized (this.reqEvenQueue) {
            this.reqEvenQueue.wait();
        }
    }

    public void reqQueueNotify() {
        synchronized (this.reqEvenQueue) {
            this.reqEvenQueue.notifyAll();
        }
    }
}
