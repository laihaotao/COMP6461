package assignment1;

import assignment1.cmd.CmdParser;
import assignment1.request.HttpRequest;
import assignment1.response.HttpResponse;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-08
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpClient {

    public static void main(String[] args) {
        int times = 0, max = 5;
        CmdParser parser = new CmdParser(args);
        HttpRequest request = parser.getHolder().getRequestInstance();
        request.send();
        System.out.println(parser.getHolder().toString());
        HttpResponse response = request.getConnection().receive();
        while (response.isRedirected && times < max) {
            args[args.length - 1] = response.location;
            parser = new CmdParser(args);
            request = parser.getHolder().getRequestInstance();
            request.send();
            System.out.println(parser.getHolder().toString());
            response = request.getConnection().receive();
            times += 1;
        }
        if (!response.isRedirected) {System.out.println(response.toString());}
        else {System.out.println("Exceed the redirection limit");}
    }

}
