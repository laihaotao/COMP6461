package assignment1.common;

import assignment1.request.*;
import com.alibaba.fastjson.JSON;

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

    public String url;
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

    @Override
    public String toString() {
        OutputBean output = new OutputBean();
        output.setArgs(args);
        output.setHeader(header);
        output.setUrl(url);
        if (method == RequestMethod.POST) {
            if (hasInlineData) {
                output.setData(argsStr);
            }
            if (hasFileDate) {
                output.setFiles(argsStr);
            }
        }
        return JSON.toJSONString(output);
    }
}
