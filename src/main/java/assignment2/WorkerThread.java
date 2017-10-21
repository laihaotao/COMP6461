package assignment2;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class WorkerThread implements Runnable {

    private EventManager        eventManager;

    public WorkerThread(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void run() {
        while (true) {

            if (eventManager.isReqQueueEmpty()) {
                try {
                    eventManager.reqQueueWait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            while (!eventManager.isReqQueueEmpty()) {
                Event event = eventManager.deReqEventQueue();
                this.reqEventHandler(event);
            }

        }
    }


    private void reqEventHandler(Event event) {
        if (event instanceof RequestEvent) {
            RequestEvent reqEvent = (RequestEvent) event;
            String path = reqEvent.getRequest().targetPath;
            // Todo: read content of the file with that path
        }
    }
}
