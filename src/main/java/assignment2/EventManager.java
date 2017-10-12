package assignment2;

import java.util.LinkedList;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class EventManager {

    private LinkedList<Event> reqEvenQueue;
    private LinkedList<Event> resEvenQueue;

    public EventManager() {
        this.reqEvenQueue = new LinkedList<>();
        this.resEvenQueue = new LinkedList<>();
    }

    public synchronized void enReqEventQueue(Event event) {
        this.reqEvenQueue.addLast(event);
    }

    public synchronized void enResEventQueue(Event event) {
        this.resEvenQueue.add(event);
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
}
