package assignment1.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public enum HeaderKey {

    // request header
    AUTHORIZATION("Authorization", HeaderKey.REQUEST),
    REFERER("Referer", HeaderKey.REQUEST),
    FROM("From", HeaderKey.REQUEST),
    USER_AGENT("User-Agent", HeaderKey.REQUEST),
    IF_MODIFIED_SINCE("If-Modified_Since", HeaderKey.REQUEST),

    // response header
    LOCATION("Location", HeaderKey.RESPONSE),
    SERVER("Server", HeaderKey.RESPONSE),
    WWW_AUTHENTICATE("WWW-Authenticate", HeaderKey.RESPONSE),

    // general header
    DATE("Date", HeaderKey.GENERAL),
    PRAGMA("Pragma", HeaderKey.GENERAL),

    // entity header
    ALLOW("Allow", HeaderKey.ENTITY),
    CONTENT_ENCODING("Content-Encoding", HeaderKey.ENTITY),
    CONTENT_LENGTH("Content-Length", HeaderKey.ENTITY),
    CONTENT_TYPE("Content-Type", HeaderKey.ENTITY),
    EXPIRES("Expires", HeaderKey.ENTITY),
    LAST_MODIFIED("Last-Modified", HeaderKey.ENTITY),

    ;

    private static final int REQUEST = 1;
    private static final int RESPONSE = 2;
    private static final int GENERAL = 3;
    private static final int ENTITY = 4;
    private int type;

    public String name;
    public static Map<String, HeaderKey> generalHeaderMap = new HashMap<>();
    public static Map<String, HeaderKey> requestHeaderMap = new HashMap<>();
    public static Map<String, HeaderKey> responseHeaderMap = new HashMap<>();
    public static Map<String, HeaderKey> entityHeaderMap = new HashMap<>();

    HeaderKey(String name, int type) {
        this.name = name;
        this.type = type;
    }

    static {
        for (HeaderKey key : HeaderKey.values()) {
            switch (key.type) {
                case REQUEST:
                    requestHeaderMap.put(key.name, key);
                    break;
                case RESPONSE:
                    responseHeaderMap.put(key.name, key);
                    break;
                case GENERAL:
                    generalHeaderMap.put(key.name, key);
                    break;
                case ENTITY:
                    entityHeaderMap.put(key.name, key);
            }
        }
    }
}
