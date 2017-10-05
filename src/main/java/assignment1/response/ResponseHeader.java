package assignment1.response;

import assignment1.common.HeaderKey;

import java.util.HashMap;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-23
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ResponseHeader {

    private HashMap<String, String> map;

    public ResponseHeader() {
        map = new HashMap<>();
    }

    public void add(String key, String value) {
        key = HeaderKey.getRightFormatKey(key.toLowerCase());
        map.put(key, value);
    }

    public String get(String key) {
        key = HeaderKey.getRightFormatKey(key.toLowerCase());
        return map.get(key);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
                builder.append(key).append(": ").append(map.get(key)).append("\n");
        }
        return builder.toString();
    }
}
