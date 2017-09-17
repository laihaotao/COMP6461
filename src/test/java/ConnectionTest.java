import assignment1.transmission.Connection;
import org.junit.Test;

import java.io.IOException;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-17
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ConnectionTest {

    @Test
    public void testSend() {
        try {
            Connection connection;
            connection = new Connection("localhost", 8080);
            String str = "hello world";
            connection.send(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
