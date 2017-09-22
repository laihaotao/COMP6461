package assignment1.request;

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
        RequestLine requestLine = new RequestLine(holder.method, holder.path);
        builder.append(requestLine.toString());
        if (holder.hasHeader) {
            RequestHeader requestHeader = new RequestHeader(holder.method, holder.header);
            builder.append(requestHeader.toString());
        }
        builder.append("\r\n");
        if (holder.hasInlineData || holder.hasFileDate) {
            RequestBody requestBody = new RequestBody(holder.argsStr);
            builder.append(requestBody.toString());
        }
        builder.append("\r\n");
    }

}
