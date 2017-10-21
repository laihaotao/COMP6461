package assignment2;

import assignment2.cmd.CmdParser;
import assignment2.common.ParamHolder;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-21
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpFs {

    public static void main(String[] args) {

        CmdParser    parser  = new CmdParser(args);
        ParamHolder  holder  = parser.getParamHolder();
        EventManager manager = new EventManager();

        try {

            Thread server = new Thread(new ServerThread(manager, holder));
            Thread worker = new Thread(new WorkerThread(manager));

            server.start();
            worker.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
