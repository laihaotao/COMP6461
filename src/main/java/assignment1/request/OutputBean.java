package assignment1.request;

import assignment1.common.HeaderKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-21
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class OutputBean {

    private RequestMethod method;
    private String data;
    private String files;
    private String url;
    private HashMap<HeaderKey, String> header = new HashMap<>();
    private Map<String, String> args = new HashMap<>();

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<HeaderKey, String> getHeader() {
        return header;
    }

    public void setHeader(HashMap<HeaderKey, String> header) {
        this.header = header;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }
}
