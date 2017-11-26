package assignment2_2;

import assignment2.file.WriteDataToFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


@SuppressWarnings("Duplicates")
public class RequestHandler {

    public HttpResponse requestHandler(HttpRequest request) {
        String method = request.method.toLowerCase();
        String path   = request.targetPath;
        int    code   = 200;
        File   file   = new File(path);
        HttpResponse response = new HttpResponse(request.version);

        if ("get".equals(method)) {

            // if the request path point to a file, return the content of that file
            if (file.isFile() && !file.isHidden()) {

                try {
                    response.setFileBody(new FileInputStream(file));
                    response.header.put(
                            "Content-Disposition",
                            "attachment;filename=" + file.getName()
                    );

                    //match file type and add Content-Type header
                    String fileSuffix = file.getName()
                            .substring(file.getName().lastIndexOf('.') + 1);
                    fillContentTypeHeader(response, fileSuffix);

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
                response.strBody = this.packFileList2String(list);
                response.header.put("Content-Disposition", "inline");
            }

            // if the file path is wrong
            else if (!file.exists()){
                code = 404;
            }
        }
        // if the request is a post, write the fileBody to a file with specified name
        else if ("post".equals(method)) {
            String postBody = request.body;
            try {
                WriteDataToFile.writeData(postBody, path);
                code = 200;
            } catch (IOException e) {
                code = 500;
                e.printStackTrace();
            }
        }

        response.code = code;
        return response;
    }

    private void fillContentTypeHeader(HttpResponse response, String fileSuffix) {
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
    }

    private String packFileList2String(File[] list) {
        StringBuilder builder = new StringBuilder();
        for (File f : list) {
            builder.append(f.getName()).append("\n");
        }
        return builder.toString();
    }
}
