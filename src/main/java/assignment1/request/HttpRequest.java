package assignment1.request;

import assignment1.common.ParamHolder;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public abstract class HttpRequest {

    public HttpRequest(ParamHolder holder) {

    }

    public abstract void send();
}
