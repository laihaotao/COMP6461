package assignment1.request;

import assignment1.common.ParamHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class RequestHeader {

    private boolean isDebug = false;
    private ParamHolder holder;

    public RequestHeader(ParamHolder holder) {
        this.holder = holder;
    }

    public String printHeader() {
        return toUrlEncoded();
    }

    @Override
    public String toString() {
        addDefaultHeader();
        return toUrlEncoded();
    }

    private String toUrlEncoded() {
        StringBuilder builder = new StringBuilder();
        for (String key : holder.header.keySet()) {
            builder.append(key).append(": ").append(holder.header.get(key)).append("\r\n");
        }
        return builder.toString();
    }

    private void addDefaultHeader() {
        // if user input header does not contain user agent,
        // add default user agent
        if (!holder.header.containsKey("User-Agent")) {
            holder.header.put("User-Agent", "COMP6461-httpc");
        }
//        if (!holder.header.containsKey("Host")) {
//            holder.header.put("Host", holder.host);
//        }
//        // add time stamp
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yy hh:mm:ss z");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String output = formatter.format(today);
        holder.header.put("Date", output);
        holder.header.put("Accept", "*/*");
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }
}
