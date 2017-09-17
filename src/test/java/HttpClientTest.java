import assignment1.cmd.CmdParser;
import assignment1.request.HttpRequest;
import org.junit.Test;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-17
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HttpClientTest {

    @Test
    public void testGet() {
        String args[] = {"httpc", "get", "-v",
                "-h", "User-Agent:httpc",
                "-h", "Date:somethingData",
                "-h", "Allow:somethingAllow",
                "http://localhost:8080/talk?user=eric&pw=1234"};

        CmdParser parser = new CmdParser(args);
        HttpRequest request = parser.getHolder().getRequestInstance();
        request.send();
    }

    @Test
    public void testPostInlineData() {
        String args[] = {"httpc", "get", "-v",
                "-h", "User-Agent:httpc",
                "-h", "Date:somethingData",
                "-h", "Allow:somethingAllow",
                "-d", "user=eric&pw=1234",
                "http://localhost:8080/talk"};

        CmdParser parser = new CmdParser(args);
        HttpRequest request = parser.getHolder().getRequestInstance();
        request.send();
    }

    @Test
    public void testPostFileData() {
        String args[] = {"httpc", "get", "-v",
                "-h", "User-Agent:httpc",
                "-h", "Date:somethingData",
                "-h", "Allow:somethingAllow",
                "-f", "/Users/ERIC_LAI/IdeaProjects/COMP6461/src/main/resources/post_test_data.txt",
                "http://localhost:8080/talk"};

        CmdParser parser = new CmdParser(args);
        HttpRequest request = parser.getHolder().getRequestInstance();
        request.send();
    }

    @Test
    public void testBaidu() {
        String args[] = {"httpc", "get", "-v",
                "-h", "User-Agent:httpc",
                "http://www.baidu.com/"};

        CmdParser parser = new CmdParser(args);
        HttpRequest request = parser.getHolder().getRequestInstance();
        request.send();
    }
}
