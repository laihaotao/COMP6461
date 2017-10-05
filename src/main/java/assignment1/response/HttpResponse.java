package assignment1.response;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpResponse {

    private ResponseLine responseLine;
    private ResponseHeader responseHeader;
    private ResponseBody responseBody;

    public HttpResponse(ResponseLine responseLine,
                        ResponseHeader responseHeader,
                        ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return responseLine.toString() + "\r\n" + responseHeader.toString() + "\r\n" + responseBody.toString();
    }
}
