package assignment1.request;

import assignment1.common.ParamHolder;

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

    }
}