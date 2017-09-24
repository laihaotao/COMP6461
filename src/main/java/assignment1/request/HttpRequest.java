package assignment1.request;

import assignment1.common.HeaderKey;
import assignment1.common.ParamHolder;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public abstract class HttpRequest {

    protected ParamHolder holder;
    protected StringBuilder builder;

    public HttpRequest(ParamHolder holder) {
        this.holder = holder;
        this.builder = new StringBuilder();
    }

    public abstract void send();

    protected void buildRequest() {
        RequestBody requestBody = null;
        RequestLine requestLine = new RequestLine(holder.method, holder.path);
        builder.append(requestLine.toString());

        if (holder.hasInlineData || holder.hasFileDate) {
            // if we are in a post request, need to set the Content-Length header
            requestBody = new RequestBody(holder.argsStr);
            holder.header.put(HeaderKey.CONTENT_LENGTH, String.valueOf(requestBody.length));
        }

        if (holder.hasHeader) {
            RequestHeader requestHeader = new RequestHeader(holder.method, holder.header);
            builder.append(requestHeader.toString());
        }
        builder.append("\r\n");

        if (requestBody != null && (holder.hasInlineData || holder.hasFileDate)) {
            builder.append(requestBody.toString());
        }
//        builder.append("\r\n");
    }

}
