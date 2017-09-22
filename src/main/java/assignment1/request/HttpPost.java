package assignment1.request;

import assignment1.common.ParamHolder;
import assignment1.transmission.Connection;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpPost extends HttpRequest {

    RequestBody requestBody;

    public HttpPost(ParamHolder holder) {
        super(holder);
    }

    @Override
    public void send() {
        buildRequest();
        String content = builder.toString();
        try {
            Connection connection = new Connection(holder.host, Integer.parseInt(holder.port));
            connection.send(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
