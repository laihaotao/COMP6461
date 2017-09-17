package assignment1.cmd;

import assignment1.common.HeaderKey;
import assignment1.common.ParamHolder;
import assignment1.request.RequestMethod;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.List;

/**
 * Author:  Kexin Zhu, Eric(Haotao) Lai
 * Date:    2017-09-16
 */


public class CmdParser {

    private ParamHolder holder;
    private String[] args;

    public CmdParser(String[] args) {
        this.args = args;
        process();
    }

    public void process() {
        String url = args[args.length - 1];
        String method = args[1];
        if (beginWithHttp(url) && validHttpMethod(method)) {
            holder.url = url;

            furtherProcess();
        } else {
            // TODO: handle cmd url input error
        }
    }

    public void furtherProcess() {
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

        if (opts.has("h")) {
            holder.hasHeader = true;
            List<String> tmpList = (List<String>) opts.valuesOf("h");
            for (String pair : tmpList) {
                if (pair.contains(":")) {
                    String key = pair.split(":")[0];
                    String value = pair.split(":")[1];
                    if (isValidRequestHeader(key.toLowerCase())) {
                        key = key.replaceAll("-", "_");
                        holder.header.put(HeaderKey.valueOf(key.toUpperCase()), value);
                    }
                } else {
                    // TODO: handle cmd header input error
                }
            }

        }

        if (opts.has("d")) {
            holder.hasInlineData = true;
            holder.data = (String) opts.valueOf("d");

        }

        if (opts.has("f")) {
            holder.hasFileDate = true;
            holder.filePath = (String) opts.valueOf("f");
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
        String prefix = url.substring(0, 6);
        return "http://".equals(prefix);
    }

    private boolean isValidRequestHeader(String key) {
        String firstLetter = String.valueOf(key.charAt(0)).toUpperCase();
        key = firstLetter + key.substring(1);
        return HeaderKey.generalHeaderMap.containsKey(key)
                || HeaderKey.requestHeaderMap.containsKey(key)
                || HeaderKey.entityHeaderMap.containsKey(key);
    }

    private boolean isValidResponseHeader(String key) {
        String firstLetter = String.valueOf(key.charAt(0)).toUpperCase();
        key = firstLetter + key.substring(1);
        return HeaderKey.generalHeaderMap.containsKey(key)
                || HeaderKey.responseHeaderMap.containsKey(key)
                || HeaderKey.entityHeaderMap.containsKey(key);
    }

}
