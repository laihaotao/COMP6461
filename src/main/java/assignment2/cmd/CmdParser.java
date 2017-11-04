package assignment2.cmd;

import assignment2.common.ParamHolder;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Created by heart on 2017-10-15.
 */
public class CmdParser {

    private ParamHolder paramHolder;
    private String[] args;

    public CmdParser(String[] args) {
        this.args = args;
        paramHolder = new ParamHolder();
        if (args[0].equals("httpfs")) {
            process();
        } else {
            System.out.println("Wrong input!");
        }
    }

    private void process() {

        OptionParser parser = new OptionParser();

        parser.accepts("v", "verbose").withOptionalArg();
        parser.accepts("p", "portNumber").withOptionalArg();
        parser.accepts("d", "directory").withOptionalArg();

        OptionSet opts = parser.parse(args);
        paramHolder.isVerbose = opts.has("v");

        if (opts.has("p")) {
            paramHolder.portNumber = Integer.parseInt((String) opts.valueOf("p"));
        }
        if (opts.has("d")) {
            paramHolder.hasDirectory = true;
            paramHolder.directory = (String) opts.valueOf("d");
        }
//        System.out.println(paramHolder.isVerbose);
//        System.out.println(paramHolder.portNumber);
//        System.out.println(paramHolder.directory);

    }

    public ParamHolder getParamHolder() {
        return paramHolder;
    }
}

