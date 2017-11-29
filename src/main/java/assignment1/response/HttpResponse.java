package assignment1.response;

import assignment1.common.ParamHolder;
import assignment1.transmission.Connection;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpResponse {

    public boolean isRedirected;
    public String location;
    private ResponseLine responseLine;
    private ResponseHeader responseHeader;
    private ResponseBody responseBody;

    public HttpResponse(boolean isRedirected, String location,
                        ResponseLine responseLine,
                        ResponseHeader responseHeader,
                        ResponseBody responseBody) {
        this.isRedirected = isRedirected;
        this.location = location;
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public HttpResponse(ResponseLine responseLine,
                        ResponseHeader responseHeader,
                        ResponseBody responseBody) {
        this.isRedirected = false;
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        String str = "";
        if (responseLine != null) str += responseLine.toString() + "\n";
        if (responseHeader != null) str += responseHeader.toString() + "\n";
        if (responseBody != null) str += responseBody.toString() + "\n";
        return str;
    }
}
