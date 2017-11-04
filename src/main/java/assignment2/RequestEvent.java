package assignment2;

import assignment2.common.ParamHolder;
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
    private ParamHolder holder;

    public RequestEvent(String rawData, SocketChannel from, ParamHolder holder) {
        super(rawData, from);
        this.request = new HttpRequest();
        this.holder  = holder;
        this.parseRawData();
    }

    private void parseRawData() {
        String   all         = this.rawData;
        String   requestLine = all.substring(0, all.indexOf('\r'));
        String[] arr         = requestLine.split("\\s+");
        request.method       = arr[0];
        request.path         = arr[1];
        request.version      = arr[2];
        String[] res         = all.split("[\r\n]+");
        int      i           = 1;
        for (; i < res.length; i++) {
            String resHeader = res[i];
            if (resHeader.contains(":")) {
                int idx = resHeader.indexOf(':');
                String key = resHeader.substring(0, idx).trim();
                String value = resHeader.substring(idx + 1).trim();
                request.header.put(key, value);
            } else {
                break;
            }
        }
        // it means all the remaining data is assignment2.response body
        StringBuilder bodyData = new StringBuilder();
        for (; i < res.length; i++) {
            bodyData.append(res[i]);
        }

        request.body = bodyData.toString();
        request.targetPath  = holder.directory + request.path;
    }

    public HttpRequest getRequest() {
        return request;
    }
}
