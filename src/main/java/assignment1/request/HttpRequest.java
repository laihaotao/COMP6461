package assignment1.request;

import assignment1.common.ParamHolder;
import assignment1.transmission.Connection;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpRequest {

    protected ParamHolder holder;
    protected StringBuilder builder;
    protected Connection connection;

    public HttpRequest(ParamHolder holder) {
        this.holder = holder;
    }

    public void send() {
        buildRequest();
        String content = builder.toString();
        try {
            if (holder.hasOutputFile) {connection = new Connection(holder.outputFileName);}
            else {connection = new Connection();}
            connection.send(content, holder.host, Integer.parseInt(holder.port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildRequest() {
        builder = new StringBuilder();
        RequestBody requestBody = null;
        RequestLine requestLine = new RequestLine(holder.method, holder.path);
        builder.append(requestLine.toString());

        if (holder.hasInlineData || holder.hasFileDate) {
            // if we are in a post request, need to set the Content-Length header
            requestBody = new RequestBody(holder.argsStr);
            holder.header.put("Content-Length", String.valueOf(requestBody.length));
        }

        if (!holder.header.isEmpty()) {
            RequestHeader requestHeader = new RequestHeader(holder);
            builder.append(requestHeader.toString());
        }
        builder.append("\r\n");

        if (requestBody != null && (holder.hasInlineData || holder.hasFileDate)) {
            builder.append(requestBody.toString());
        }
//        builder.append("\r\n");
    }

    public Connection getConnection() {
        return connection;
    }
}
