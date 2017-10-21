package assignment2;

import assignment2.file.ReadDataFromFile;

import java.io.IOException;

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
                try {
                    this.reqEventHandler(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private void reqEventHandler(Event event) throws IOException {
        if (event instanceof RequestEvent) {
            RequestEvent reqEvent = (RequestEvent) event;
            String path = reqEvent.getRequest().targetPath;
            // Todo: read content of the file with that path to a string
            String content =  ReadDataFromFile.readFile(path);
        }
    }
}
