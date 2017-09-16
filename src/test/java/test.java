import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;

import static java.util.Arrays.asList;
class test {
    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser();

        parser.acceptsAll(asList("version", "v"), "protocal version")
                .withOptionalArg();
        parser.acceptsAll(asList("header", "h"), "request head")
                .withOptionalArg()
                .defaultsTo("localhost");
        parser.acceptsAll(asList("inline_data", "d"), "inline data")
                .withOptionalArg();
        parser.acceptsAll(asList("file", "f"), "source file of request")
                .withOptionalArg();


        OptionSet opts = parser.parse(args);

        String header = (String) opts.valueOf("header");
        String version = (String) opts.valueOf("version");
        String inline_data = (String) opts.valueOf("inline_data");
        String file = (String) opts.valueOf("file");

        System.out.println(version);
        System.out.println(header);
        System.out.println(inline_data);
        System.out.println(file);
    }




}

