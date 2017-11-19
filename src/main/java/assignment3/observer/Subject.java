package assignment3.observer;

import assignment3.Packet;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public abstract class Subject {

    private List<Observer> list;

    public Subject() {
        this.list = new LinkedList<>();
    }

    public void attach(Observer observer) {
        this.list.add(observer);
    }

    protected void detach(Observer observer) {
        for (Observer o : list) {
            if (Objects.equals(o, observer)) {
                this.list.remove(observer);
            }
        }
    }

    protected void notifyObservers(NoticeMsg msg, Packet packet) {
        for (Observer observer : list) {
            observer.update(msg, packet);
        }
    }
}
