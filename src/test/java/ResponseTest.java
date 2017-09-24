import assignment1.response.HttpResponse;
import assignment1.response.ResponseHeader;
import assignment1.response.ResponseLine;
import assignment1.transmission.Connection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-23
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class ResponseTest {

    private static final Logger logger = LoggerFactory.getLogger(ResponseTest.class);


    @Test
    public void test() {
        receive();
    }

    public void receive() {
        // the idea here is we need to read the header and parse them to
        // get the Content-Length's value and decide how many time I need
        // to read for the remaining message

        final int BUF_SIZE = 1024;
        ByteBuffer buffer_1 = ByteBuffer.allocate(BUF_SIZE);

        String str = "hello world";
        byte[] strBytes = str.getBytes();
        logger.debug("strBtyes: {}", strBytes);

        byte[] bytes = new byte[BUF_SIZE];
        ByteBuffer curBuffer = buffer_1;
        curBuffer.put(strBytes, 0, strBytes.length);
        curBuffer.flip();
        curBuffer.get(bytes, 0, strBytes.length);
        logger.debug("bytes: {}", bytes);

        String data = new String(bytes);
        logger.debug("current data without any process: {}", data);
        /*
        // split the string by "\r\n"
        String[] res = data.split("[\r\n]+");
        logger.debug("after splitting the response by '\r\n', we have {} part", res.length);
        if (res.length > 1) {
            // process the response line
            ResponseLine line = new ResponseLine(res[0]);
            httpResponse.setResponseLine(line);
            // process the response header
            ResponseHeader header = new ResponseHeader();
            int i = 1;
            for (; i < res.length; i++) {
                String resHeader = res[i];
                if (resHeader.contains(":")) {
                    String[] tmp = resHeader.split(":");
                    String key = tmp[0];
                    String value = tmp[1];
                    header.add(key, value);
                } else {
                    logger.error("Invalid response header format");
                    break;
                }
            }
            // it means all the remaining data is response body
            String bodyData = "";
            for (; i < res.length; i++) {
                bodyData += res[i];
            }
            logger.debug("Response body data: {}", bodyData);

            // process the response body, if we have response body, their
            // must be a Content-Length in the header


            //int contentLen = Integer.parseInt(header.get("Content-Type"));

        }
        */
    }

}
