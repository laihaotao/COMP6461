package assignment2_2;

import java.util.HashMap;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-21
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpRequest {

    public String targetPath;
    public String method;
    public String path;
    public String version;
    public HashMap<String,String> header = new HashMap<>();
    public String body;

}
