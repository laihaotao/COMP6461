package assignment1.response;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-23
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ResponseBody {

    private String content;

    public ResponseBody(String str) {
        this.content = str;
    }

    @Override
    public String toString() {
        return content;
    }
}
