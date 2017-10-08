package assignment1.request;

import java.io.UnsupportedEncodingException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class RequestBody {

    private boolean isDebug = false;
    public int length;
    public String argsStr;

    public RequestBody(String argsStr) {
        this.argsStr = argsStr;
        try {
            this.length = argsStr.getBytes("utf-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "\r\n" + argsStr;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }
}
