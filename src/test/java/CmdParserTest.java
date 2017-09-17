import assignment1.cmd.CmdParser;
import assignment1.common.HeaderKey;
import assignment1.common.ParamHolder;
import assignment1.request.RequestMethod;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class CmdParserTest {

    @Test
    public void testValidGet() {
        String args[] = {"httpc", "get", "-v",
                "-h", "User-Agent:httpc",
                "-h", "Date:somethingData",
                "-h", "Allow:somethingAllow",
                "http://laihaotao.me/talk?user=eric&pw=1234"};
        CmdParser parser = new CmdParser(args);
        ParamHolder holder = parser.getHolder();

        assertEquals(RequestMethod.GET, holder.method);
        assertTrue(holder.isVerbose);
        assertTrue(holder.hasHeader);

        Map<HeaderKey, String> expectedHeader = new HashMap<>();
        expectedHeader.put(HeaderKey.USER_AGENT, "httpc");
        expectedHeader.put(HeaderKey.DATE, "somethingData");
        expectedHeader.put(HeaderKey.ALLOW, "somethingAllow");
        assertEquals(expectedHeader, holder.header);

        assertEquals("laihaotao.me", holder.host);
        assertEquals("/talk", holder.path);

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("user", "eric");
        expectedMap.put("pw", "1234");
        assertEquals(expectedMap, holder.args);

    }


    @Test
    public void testValidPostInlineData() {
        String args[] = {"httpc", "post", "-v",
                "-h", "User-Agent:httpc",
                "-h", "Date:somethingData",
                "-h", "Allow:somethingAllow",
                "-d", "user=eric&pw=1234",
                "http://laihaotao.me/talk"};
        CmdParser parser = new CmdParser(args);
        ParamHolder holder = parser.getHolder();

        assertEquals(RequestMethod.POST, holder.method);
        assertTrue(holder.isVerbose);
        assertTrue(holder.hasHeader);

        Map<HeaderKey, String> expectedHeader = new HashMap<>();
        expectedHeader.put(HeaderKey.USER_AGENT, "httpc");
        expectedHeader.put(HeaderKey.DATE, "somethingData");
        expectedHeader.put(HeaderKey.ALLOW, "somethingAllow");
        assertEquals(expectedHeader, holder.header);

        assertEquals("laihaotao.me", holder.host);
        assertEquals("/talk", holder.path);

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("user", "eric");
        expectedMap.put("pw", "1234");
        assertEquals(expectedMap, holder.args);
    }

    @Test
    public void testValidPostFileData() {
        String args[] = {"httpc", "post", "-v",
                "-h", "User-Agent:httpc",
                "-h", "Date:somethingData",
                "-h", "Allow:somethingAllow",
                "-f", "/Users/ERIC_LAI/IdeaProjects/COMP6461/src/main/resources/post_test_data.txt",
                "http://laihaotao.me/talk"};
        CmdParser parser = new CmdParser(args);
        ParamHolder holder = parser.getHolder();

        assertEquals(RequestMethod.POST, holder.method);
        assertTrue(holder.isVerbose);
        assertTrue(holder.hasHeader);

        Map<HeaderKey, String> expectedHeader = new HashMap<>();
        expectedHeader.put(HeaderKey.USER_AGENT, "httpc");
        expectedHeader.put(HeaderKey.DATE, "somethingData");
        expectedHeader.put(HeaderKey.ALLOW, "somethingAllow");
        assertEquals(expectedHeader, holder.header);

        assertEquals("laihaotao.me", holder.host);
        assertEquals("/talk", holder.path);

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("user", "eric");
        expectedMap.put("pw", "1234");
        assertEquals(expectedMap, holder.args);
    }
}
