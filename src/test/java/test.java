import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.util.List;
import java.lang.*;

import static java.util.Arrays.asList;
class test {
    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser();

        parser.accepts( "v", "for test")
                .withOptionalArg();
        parser.accepts("h", "request head")
                .withOptionalArg()
                .defaultsTo("localhost");
        parser.accepts( "d", "inline data")
                .withOptionalArg();
        parser.accepts( "f", "source file of request")
                .withOptionalArg();


        OptionSet opts = parser.parse(args);

        String version = (String) opts.valueOf("v");
        List<String> header;
        header = (List<String>) opts.valuesOf("h");
        String inline_data = (String) opts.valueOf("d");
        String file = (String) opts.valueOf("f");


        System.out.println(version);
        for(int i = 0 ; i < header.size() ; i++) {
            System.out.println(header.get(i));
        }
        System.out.println(inline_data);
        System.out.println(file);


    }




}

