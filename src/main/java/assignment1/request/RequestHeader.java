package assignment1.request;

import assignment1.common.HeaderKey;
import assignment1.common.ParamHolder;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Objects;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class RequestHeader {

    private boolean isDebug = false;
    private RequestMethod method;
    private HashMap<HeaderKey, String> map;


    public RequestHeader(RequestMethod method, HashMap<HeaderKey, String> map) {
        this.method = method;
        this.map = map;
    }

    @Override
    public String toString() {
        return toUrlEncoded();
    }

    private String toUrlEncoded() {
        StringBuilder builder = new StringBuilder();
        for (HeaderKey key : map.keySet()) {
            if (isDebug) {
                builder.append(key.name).append(": ").append(map.get(key)).append("\n");
            } else {
                builder.append(key.name).append(": ").append(map.get(key)).append("\r\n");
            }
        }
        return builder.toString();
    }

    private String toJson() {
        return JSON.toJSONString(map);
    }

    private String toXml() {
        return null;
    }

    private String toPlainText() {
        return null;
    }

    private void addDefaultHeader() {

    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }
}
