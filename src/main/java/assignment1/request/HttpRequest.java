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

    public HttpRequest(ParamHolder holder) {
        this.holder = holder;
    }

    public abstract void send();

    protected void buildRequest() {
        RequestLine requestLine = new RequestLine(holder.method, holder.path);
    }
}
