package assignment1.request;

import assignment1.common.HeaderKey;

import java.util.HashMap;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class RequestHeader {

    private boolean isDebug = false;
    private HashMap<HeaderKey, String> map;

    public RequestHeader(HashMap<HeaderKey, String> map) {
        this.map = map;
    }

//    public void add(HeaderKey key, String value) {
//        map.put(key, value);
//    }
//
//    public void remove(HeaderKey key) {
//        map.remove(key.name);
//    }

    @Override
    public String toString() {
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

    public void setDebug(boolean debug) {
        isDebug = debug;
    }
}
