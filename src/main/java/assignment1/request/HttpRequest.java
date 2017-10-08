package assignment1.request;

import assignment1.common.ParamHolder;
import assignment1.transmission.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpRequest {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private ParamHolder holder;
    private StringBuilder builder;
    private Connection connection;

    private RequestLine requestLine;
    private RequestHeader requestHeader;
    private RequestBody requestBody;


    public HttpRequest(ParamHolder holder, Connection connection) {
        this.holder = holder;
        this.connection = connection;
    }

    public void send() throws IOException {
        buildRequest();
        String content = builder.toString();
        connection.send(content, holder.host, Integer.parseInt(holder.port));
        if (holder.isVerbose) {
            System.out.print(requestLine.toString());
            System.out.print(requestHeader.printHeader());
            if (requestBody == null) {System.out.println();}
            else {System.out.print(requestBody.toString());}
        }
        System.out.println((holder.toString()));
    }

    private void buildRequest() {
        builder = new StringBuilder();
        requestBody = null;
        requestLine = new RequestLine(holder.method, holder.path);
        builder.append(requestLine.toString());

        if (holder.hasInlineData || holder.hasFileDate) {
            // if we are in a post request, need to set the Content-Length header
            requestBody = new RequestBody(holder.argsStr);
            holder.header.put("Content-Length", String.valueOf(requestBody.length));
        }

        if (!holder.header.isEmpty()) {
            requestHeader = new RequestHeader(holder);
            builder.append(requestHeader.toString());
        }
        builder.append("\r\n");

        if (requestBody != null && (holder.hasInlineData || holder.hasFileDate)) {
            builder.append(requestBody.toString());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
