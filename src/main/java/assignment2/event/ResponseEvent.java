package assignment2.event;

import assignment2.response.HttpResponse;

import java.nio.channels.SocketChannel;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-21
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ResponseEvent extends Event {

    private HttpResponse response;

    public ResponseEvent(String rawData, SocketChannel from, HttpResponse response) {
        super(rawData, from);
        this.response = response;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
