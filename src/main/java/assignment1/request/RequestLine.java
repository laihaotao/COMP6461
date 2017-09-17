package assignment1.request;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class RequestLine {

    private RequestMethod method;
    private String path;
    private String version;

    public RequestLine(RequestMethod method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public RequestLine(RequestMethod method, String path) {
        this.method = method;
        this.path = path;
        this.version = "HTTP/1.0";
    }

    @Override
    public String toString() {
        return method.toString() + " " + path + " " + version + "\r\n";
    }
}
