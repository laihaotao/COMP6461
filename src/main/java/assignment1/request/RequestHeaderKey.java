package assignment1.request;

import java.util.HashMap;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public enum RequestHeaderKey {

    ALLOW("Allow"),
    AUTHORIZATION("Authorization"),
    CONTENT_ENCODING("Content-Encoding"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    DATE("Date"),
    EXPIRES("Expires"),
    FROM("From"),
    IF_MODIFIED_SINCE("If-Modified_Since"),
    LAST_MODIFIED("Last-Modified"),
    LOCATION("Location"),
    PRAGMA("Pragma"),
    REFERER("Referer"),
    SERVER("Server"),
    USER_AGENT("User-Agent"),
    WWW_AUTHENTICATE("WWW-Authenticate"),;

    public String name;

    RequestHeaderKey(String name) {
        this.name = name;
    }
}
