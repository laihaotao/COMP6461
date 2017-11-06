package assignment2;

import assignment2.event.RequestEvent;
import assignment2.event.ResponseEvent;
import assignment2.file.ReadDataFromFile;
import assignment2.file.WriteDataToFile;
import assignment2.response.HttpResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class WorkerThread implements Runnable {

    private EventManager eventManager;

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
                RequestEvent event = (RequestEvent) eventManager.deReqEventQueue();
                this.reqEventHandler(event);
            }

        }
    }

    private void reqEventHandler(RequestEvent reqEvent) {
        String method = reqEvent.getRequest().method.toLowerCase();
        String path   = reqEvent.getRequest().targetPath;

        File   file   = new File(path);
        int    code   = 200;
        String body   = null;
        HttpResponse response = new HttpResponse(reqEvent.getRequest().version);

        if ("get".equals(method)) {

            // if the request path point to a file, return the content of that file
            if (file.isFile() && !file.isHidden()) {

                try {
                    body = ReadDataFromFile.readFile(file);
                    response.header.put(
                            "Content-Disposition",
                            "attachment;filename=" + file.getName()
                    );
                    //match file type and add Content-Type header
                    String fileSuffix = file.getName()
                            .substring(file.getName().lastIndexOf('.') + 1);
                    switch (fileSuffix) {
                        case "css":
                        case "html":
                            response.header.put("Content-Type", "text/" + fileSuffix);
                            break;
                        case "jsp":
                            response.header.put("Content-Type", "text/html");
                            break;
                        case "txt":
                            response.header.put("Content-Type", "text/plain");
                            break;
                        case "jpg":
                        case "jpeg":
                            response.header.put("Content-Type", "image/jpeg");
                            break;
                        case "img":
                            response.header.put("Content-Type", "application/x-img");
                            break;
                        case "pdf":
                            response.header.put("Content-Type", "application/pdf");
                            break;
                        case "png":
                            response.header.put("Content-Type", "application/x-png");
                            break;
                        case "xsd":
                        case "xql":
                        case "xslt":
                        case "biz":
                            response.header.put("Content-Type", "text/xml");
                            break;
                    }

                } catch (IOException e) {
                    if (e instanceof FileNotFoundException) {
                        code = 404;
                    }
                    e.printStackTrace();
                }
            }

            // if the file point a directory, return the list of items inside that directory
            else if (file.isDirectory() && !file.isHidden()) {
                File[] list = file.listFiles((dir, name) -> name.charAt(0) != '.');
                body = this.packFileList2String(list);
                response.header.put("Content-Disposition", "inline");
            }

            // if the file path is wrong
            else if (!file.exists()){
                code = 404;
            }
        }
        // if the request is a post, write the body to a file with specified name
        else if ("post".equals(method)) {
            String postBody = reqEvent.getRequest().body;
            try {
                WriteDataToFile.writeData(postBody, path);
                code = 200;
            } catch (IOException e) {
                code = 500;
                e.printStackTrace();
            }
        }

        response.body = body;
        response.code = code;

        ResponseEvent resEvent = new ResponseEvent(response.toString(), reqEvent.from);
        eventManager.enResEventQueue(resEvent);

    }

    private String packFileList2String(File[] list) {
        StringBuilder builder = new StringBuilder();
        for (File f : list) {
            builder.append(f.getName()).append("\n");
        }
        return builder.toString();
    }

}
