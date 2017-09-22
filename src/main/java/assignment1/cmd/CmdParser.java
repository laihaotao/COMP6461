package assignment1.cmd;

import assignment1.common.HeaderKey;
import assignment1.common.ParamHolder;
import assignment1.file.DataFileReader;
import assignment1.request.RequestMethod;
import com.sun.javafx.tools.packager.Param;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.List;

/**
 * Author:  Kexin Zhu, Eric(Haotao) Lai
 * Date:    2017-09-16
 */


public class CmdParser {

    private static final Logger logger = LoggerFactory.getLogger(CmdParser.class);

    private ParamHolder holder;
    private String[] args;

    public CmdParser(String[] args) {
        this.args = args;
        holder = new ParamHolder();
        process();
    }

    public ParamHolder getHolder() {
        return holder;
    }

    private void process() {
        String url = args[args.length - 1];
        String method = args[1];

//        logger.debug("request url: {}", url);
//        logger.debug("request method: {}", method);
        if (beginWithHttp(url) && validHttpMethod(method)) {
            holder.url = url;
            String noHttpUrl = url.substring(7);
            int firstSlashIdx = noHttpUrl.indexOf('/');
            String hostAndPort = noHttpUrl.substring(0, firstSlashIdx);
            if (hostAndPort.contains(":")) {
                int colonIdx = hostAndPort.indexOf(':');
                holder.host = hostAndPort.substring(0, colonIdx);
                holder.port = hostAndPort.substring(colonIdx + 1);
            } else {
                holder.host = noHttpUrl.substring(0, firstSlashIdx);
                holder.port = "80";
            }

            if (noHttpUrl.contains("?")) {
                int questionMark = noHttpUrl.indexOf('?');
                holder.path = noHttpUrl.substring(firstSlashIdx, questionMark);
                holder.argsStr = noHttpUrl.substring(questionMark + 1);
                parseArgs(holder.argsStr);
            } else {
                holder.path = noHttpUrl.substring(firstSlashIdx);
            }

            logger.debug("request host: {}", holder.host);
            logger.debug("request port: {}", holder.port);
            logger.debug("request path: {}", holder.path);
            logger.debug("request args: {}", holder.argsStr);

            furtherProcess();
        } else {
            // TODO: handle cmd url input error
            logger.error("Invalid format of your URL, please check it again");
        }
    }

    private void furtherProcess() {
        OptionParser parser = new OptionParser();

        // specify the parameter rules
        parser.accepts("v", "verbose")
                .withOptionalArg();

        parser.accepts("h", "request header")
                .withOptionalArg();

        parser.accepts("d", "inline data")
                .withOptionalArg();

        parser.accepts("f", "source file of request")
                .withOptionalArg();

        // get the optional parameter set, if the parameter with the specified
        // prefix, it will be null stored in the variable
        OptionSet opts = parser.parse(args);
        holder.isVerbose = opts.has("v");
        logger.debug("verbose flag: {}", holder.isVerbose);

        if (opts.has("h")) {
            holder.hasHeader = true;
            logger.debug("header flag: {}", true);
            List<String> tmpList = (List<String>) opts.valuesOf("h");
            for (String pair : tmpList) {
                if (pair.contains(":")) {
                    String key = pair.split(":")[0];
                    String value = pair.split(":")[1];
                    if (isValidRequestHeader(key.toLowerCase())) {
                        key = key.replace('-', '_');
                        holder.header.put(HeaderKey.valueOf(key.toUpperCase()), value);
                        logger.debug("request header pair -> {}: {}", key, value);
                    }
                } else {
                    // TODO: handle cmd header input error
                    logger.error("Invalid format of your header key value pair, please check " +
                            "again");
                }
            }
        }

        if (opts.has("d")) {
            logger.debug("inline data flag: {}", true);
            holder.hasInlineData = true;
            holder.data = (String) opts.valueOf("d");
            holder.argsStr = holder.data;
        }

        if (opts.has("f")) {
            logger.debug("file data flag: {}", true);
            holder.hasFileDate = true;
            holder.filePath = (String) opts.valueOf("f");
            try {
                holder.argsStr = new DataFileReader(holder.filePath).read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseArgs(String str) {
        String[] pair = str.split("&");
        for (String s : pair) {
            String[] res = s.split("=");
            holder.args.put(res[0], res[1]);
            logger.debug("http get request argument: {}={}", res[0], res[1]);
        }
    }

    private boolean validHttpMethod(String method) {
        if ("get".equals(method.toLowerCase())) {
            holder.method = RequestMethod.GET;
            return true;
        }
        if ("post".equals(method.toLowerCase())) {
            holder.method = RequestMethod.POST;
            return true;
        }
        return false;
    }

    private boolean beginWithHttp(String url) {
        String prefix = url.substring(0, 7);
//        logger.debug("first 7 characters of user input URL: {}", prefix);
        return "http://".equals(prefix);
    }

    private boolean isValidRequestHeader(String key) {
        key = getRightFormatKey(key);
        return HeaderKey.generalHeaderMap.containsKey(key)
                || HeaderKey.requestHeaderMap.containsKey(key)
                || HeaderKey.entityHeaderMap.containsKey(key);
    }

    private boolean isValidResponseHeader(String key) {
        key = getRightFormatKey(key);
        return HeaderKey.generalHeaderMap.containsKey(key)
                || HeaderKey.responseHeaderMap.containsKey(key)
                || HeaderKey.entityHeaderMap.containsKey(key);
    }

    private String getRightFormatKey(String key) {
        char[] chars = key.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        if (key.contains("-")) {
            // if key has '-', we need to make the first letter after '-' to be an
            // uppercase letter
            int dashIdx = key.indexOf('-');
            chars[dashIdx + 1] = Character.toUpperCase(chars[dashIdx + 1]);
        }
        return String.valueOf(chars);
    }

}
