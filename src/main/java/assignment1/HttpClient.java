package assignment1;

import assignment1.cmd.CmdParser;
import assignment1.request.HttpRequest;
import assignment1.response.HttpResponse;
import assignment1.response.ResponseBody;
import assignment1.transmission.Connection;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-08
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpClient {

    public static void main(String[] args) {

        CmdParser parser = new CmdParser(args);
        HttpRequest request = parser.getHolder().getRequestInstance();
        request.send();
        HttpResponse response = request.getConnection().receive();

    }

}
