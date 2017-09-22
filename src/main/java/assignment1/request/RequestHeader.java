package assignment1.request;

import assignment1.common.HeaderKey;
import assignment1.common.ParamHolder;
import com.alibaba.fastjson.JSON;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimeZone;

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
        addDefaultHeader();
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

    private void addDefaultHeader() {
        // add user agent
        map.put(HeaderKey.USER_AGENT, "COMP6461-httpc");

        // add time stamp
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yy hh:mm:ss z");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String output = formatter.format(today);
        map.put(HeaderKey.DATE, output);
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }
}
