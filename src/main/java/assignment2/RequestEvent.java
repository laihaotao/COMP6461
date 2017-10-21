package assignment2;

import assignment2.request.HttpRequest;

import java.nio.channels.SocketChannel;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-21
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class RequestEvent extends Event {

    private HttpRequest request;

    public RequestEvent(String rawData, SocketChannel from) {
        super(rawData, from);
    }

    private void parseRawData() {
        // Todo: parse into http format
    }

    public HttpRequest getRequest() {
        return request;
    }
}
