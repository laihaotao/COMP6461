package assignment1.common;

import assignment1.request.HttpGet;
import assignment1.request.HttpPost;
import assignment1.request.HttpRequest;
import assignment1.request.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ParamHolder {

    public RequestMethod method;
    public boolean isVerbose;

    public boolean hasHeader;
    public HashMap<HeaderKey, String> header = new HashMap<>();

    public boolean hasInlineData;
    public String data;
    public boolean hasFileDate;
    public String filePath;

    public String host;
    public String port;
    public String path;
    public String argsStr;
    public Map<String, String> args = new HashMap<>();


    public HttpRequest getRequestInstance() {
        if (method == RequestMethod.GET) {
            return new HttpGet(this);
        }
        else if (method == RequestMethod.POST) {
            return new HttpPost(this);
        }
        return null;
    }
}
