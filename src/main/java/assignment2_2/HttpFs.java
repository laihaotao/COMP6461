package assignment2_2;

import assignment2.cmd.CmdParser;
import assignment2.common.ParamHolder;
import assignment3.RUDP.ServerRUDP;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-25
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpFs {

    public static void main(String[] args) throws IOException {

        CmdParser    parser  = new CmdParser(args);
        ParamHolder  holder  = parser.getParamHolder();

        int len;
        ServerRUDP    server     = new ServerRUDP(holder.portNumber);
        ByteBuffer    buf        = ByteBuffer.allocate(1024);
        StringBuilder strBuilder = new StringBuilder();

        while ((len = server.receive(buf)) != -1) {
            byte[] raw   = buf.array();
            byte[] bytes = new byte[len];
            System.arraycopy(raw, 0, bytes, 0, len);
            String content = new String(bytes);
            strBuilder.append(content);
        }

        RequestBuilder reqBuilder = new RequestBuilder(strBuilder.toString(), holder);
        RequestHandler handler = new RequestHandler();
        HttpResponse response = handler.requestHandler(reqBuilder.getRequest());
        server.sendResponse(response);
    }
}
