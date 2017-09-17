package assignment1.request;

import assignment1.common.HeaderKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class RequestBody {

    private boolean isDebug = false;
    public Map<String, String> args = new HashMap<>();

    public RequestBody(Map<String, String> args) {
        this.args = args;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String key : args.keySet()) {
            if (isDebug) {
                builder.append(key).append(": ").append(args.get(key)).append("\n");
            } else {
                // TODO: need to modify the separator according to the content-type
                builder.append(key).append(": ").append(args.get(key)).append("&");
            }
        }
        return builder.toString();
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }
}
