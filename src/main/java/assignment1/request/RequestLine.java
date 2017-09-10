package assignment1.request;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class RequestLine {

    private RequestMethod method;
    private String url;
    private String version;

    public RequestLine(RequestMethod method, String url, String version) {
        this.method = method;
        this.url = url;
        this.version = version;
    }

    public RequestLine(RequestMethod method, String url) {
        this.method = method;
        this.url = url;
        this.version = "HTTP/1.0";
    }

    @Override
    public String toString() {
        return method.toString() + " " + url + " " + version + "\r\n";
    }
}
