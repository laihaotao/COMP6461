package assignment1;

import assignment1.cmd.CmdParser;
import assignment1.common.ParamHolder;
import assignment1.request.HttpRequest;
import assignment1.response.HttpResponse;
import assignment1.transmission.Connection;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-08
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */

public class HttpClient {

    public static void main(String[] args) throws IOException {
        int times = 0, max = 5;
        HttpResponse response = getResponse(args);
        while (response.isRedirected && times < max) {
            args[args.length - 1] = response.location;
            getResponse(args);
            times += 1;
        }
    }

    private static HttpResponse getResponse(String[] args) throws IOException {
        CmdParser parser = new CmdParser(args);
        ParamHolder holder = parser.getHolder();
        Connection connection = new Connection(holder);
        HttpRequest request = new HttpRequest(holder, connection);
        request.send();
        return connection.receive();
    }

}
