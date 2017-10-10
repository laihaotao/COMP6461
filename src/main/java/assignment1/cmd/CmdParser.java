package assignment1.cmd;

import assignment1.common.ParamHolder;
import assignment1.file.DataFileReader;
import assignment1.request.RequestMethod;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;

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

        if (Objects.equals(args[1], "help")) {
            processHelp();
            System.exit(0);
        } else {
            process();
        }

    }

    public ParamHolder getHolder() {
        return holder;
    }

    private void process() {
        String url = args[args.length - 1];
        String method = args[1];

//        logger.trace("request url: {}", url);
//        logger.trace("request method: {}", method);
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
            holder.header.put("Host", holder.host);
            if (noHttpUrl.contains("?")) {
                int questionMark = noHttpUrl.indexOf('?');
                holder.path = noHttpUrl.substring(firstSlashIdx);
                holder.argsStr = noHttpUrl.substring(questionMark + 1);
                parseArgs(holder.argsStr);
            } else {
                holder.path = noHttpUrl.substring(firstSlashIdx);
            }

            logger.trace("request host: {}", holder.host);
            logger.trace("request port: {}", holder.port);
            logger.trace("request path: {}", holder.path);
            logger.trace("request args: {}", holder.argsStr);

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

        parser.accepts("o", "output result to a separate file")
                .withOptionalArg();

        // get the optional parameter set, if the parameter with the specified
        // prefix, it will be null stored in the variable
        OptionSet opts = parser.parse(args);
        holder.isVerbose = opts.has("v");
        logger.trace("verbose flag: {}", holder.isVerbose);

        if (opts.has("h")) {
            holder.hasHeader = true;
            logger.trace("header flag: {}", true);
            List<String> tmpList = (List<String>) opts.valuesOf("h");
            for (String pair : tmpList) {
                if (pair.contains(":")) {
                    String key = pair.split(":")[0];
                    String value = pair.split(":")[1];
                    holder.header.put(key, value);
                    logger.trace("request header pair -> {}: {}", key, value);
                } else {
                    logger.error("Invalid format of your header key value pair, please check " +
                            "again");
                }
            }
        }

        if (opts.has("d")) {
            logger.trace("inline data flag: {}", true);
            holder.hasInlineData = true;
            holder.data = (String) opts.valueOf("d");
            holder.argsStr = holder.data;
        }

        if (opts.has("f")) {
            logger.trace("file data flag: {}", true);
            holder.hasFileDate = true;
            holder.filePath = (String) opts.valueOf("f");
            try {
                holder.argsStr = new DataFileReader(holder.filePath).read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (opts.has("o")) {
            holder.hasOutputFile = true;
            holder.outputFileName = (String) opts.valueOf("o");
        }
    }

    private void parseArgs(String str) {
        String[] pair = str.split("&");
        for (String s : pair) {
            String[] res = s.split("=");
            holder.args.put(res[0], res[1]);
            logger.trace("http get request argument: {}={}", res[0], res[1]);
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
        String prefix = url.trim().substring(0, 7);
//        logger.trace("first 7 characters of user input URL: {}", prefix);
        return "http://".equals(prefix);
    }

    private void processHelp() {
        String appCmd = "httpc";
        String help = "help";
        String get = "get";
        String post = "post";

        if (args.length >= 2 && appCmd.equals(args[0]) && help.equals(args[1])) {
            if (args.length == 3 && get.equals(args[2])) {
                printHelpGet();
            } else if (args.length == 3 && post.equals(args[2])) {
                printHelpPost();
            } else {
                printHelp();
            }
        }
    }

    private void printHelp() {
        Formatter formatter = new Formatter(System.out);
        System.out.println("httpc is a curl-like application but supports HTTP protocol only.\n" +
                "Usage:\n" +
                "\thttpc command [arguments]\n" +
                "The commands are:");
        formatter.format("\t%-10s %-200s\n", "get", "executes a HTTP GET request and prints the " +
                "response.");
        formatter.format("\t%-10s %-200s\n", "post", "executes a HTTP POST request and prints the" +
                " response.");
        formatter.format("\t%-10s %-200s\n", "help", "prints this screen.\n");
        System.out.println("Use \"httpc help [command]\" for more information about a command.\n");

    }

    private void printHelpGet() {
        Formatter formatter = new Formatter(System.out);
        System.out.println(
                "Usage: httpc get [-v] [-h key:value] URL\n\n" +
                        "Get executes a HTTP GET request for a given URL.\n");
        formatter.format("\t%-20s %-200s\n", "-v", "Prints the detail of the response such as " +
                "protocol, status, and headers.");
        formatter.format("\t%-20s %-200s\n", "-h key:value", "Associates headers to HTTP Request " +
                "with the format 'key:value'.");

    }

    private void printHelpPost() {
        Formatter formatter = new Formatter(System.out);
        System.out.println(
                "usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n\n" +
                        "Post executes a HTTP POST request for a given URL with inline data or " +
                        "from file.\n");
        formatter.format("\t%-20s %-200s\n", "-v", "Prints the detail of the response such as " +
                "protocol, status, and headers.");
        formatter.format("\t%-20s %-200s\n", "-h key:value", "Associates headers to HTTP Request " +
                "with the format 'key:value'.");
        formatter.format("\t%-20s %-200s\n", "-d string", "Associates an inline data to the body " +
                "HTTP POST request.");
        formatter.format("\t%-20s %-200s\n", "-f file", "Associates the content of a file to the " +
                "body HTTP POST request.");
        System.out.println("Either [-d] or [-f] can be used but not both.");
    }

}
