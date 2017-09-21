import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.text.Format;
import java.util.Formatter;
import java.util.List;
import java.lang.*;

import static java.util.Arrays.asList;
class test {
    public static void main(String[] args) throws IOException {
//        OptionParser parser = new OptionParser();
//
//        parser.accepts( "v", "for test")
//                .withOptionalArg();
//        parser.accepts("h", "request head")
//                .withOptionalArg()
//                .defaultsTo("localhost");
//        parser.accepts( "d", "inline data")
//                .withOptionalArg();
//        parser.accepts( "f", "source file of request")
//                .withOptionalArg();
//
//
//        OptionSet opts = parser.parse(args);
//
//        String version = (String) opts.valueOf("v");
//        List<String> header;
//        header = (List<String>) opts.valuesOf("h");
//        String inline_data = (String) opts.valueOf("d");
//        String file = (String) opts.valueOf("f");
//
//
//        System.out.println(version);
//        for(int i = 0 ; i < header.size() ; i++) {
//            System.out.println(header.get(i));
//        }
//        System.out.println(inline_data);
//        System.out.println(file);

        if(args[0].equals("httpc") && args[1].equals("help") && 2 == args.length) {
            printHelp();
        }
        else if (args[0].equals("httpc") && args[1].equals("help") && args[2].equals("get")&& 3 == args.length) {
            printHelpGet();
        }
        else if (args[0].equals("httpc") && args[1].equals("help") && args[2].equals("post")&& 3 == args.length) {
            printHelpPost();
        }
    }


    private static void printHelp (){
        Formatter formatter = new Formatter(System.out);
        System.out.println("httpc is a curl-like application but supports HTTP protocol only.\n" +
                "Usage:\n" +
                "\thttpc command [arguments]\n" +
                "The commands are:");
        formatter.format("\t%-10s %-200s\n", "get", "executes a HTTP GET request and prints the response.");
        formatter.format("\t%-10s %-200s\n", "post", "executes a HTTP POST request and prints the response.");
        formatter.format("\t%-10s %-200s\n", "help", "prints this screen.\n");
        System.out.println("Use \"httpc help [command]\" for more information about a command.\n");

    }
    private static void printHelpGet (){
        Formatter formatter = new Formatter(System.out);
        System.out.println(
                "Usage: httpc get [-v] [-h key:value] URL\n\n" +
                        "Get executes a HTTP GET request for a given URL.\n");
        formatter.format("\t%-20s %-200s\n", "-v", "Prints the detail of the response such as protocol, status, and headers.");
        formatter.format("\t%-20s %-200s\n", "-h key:value", "Associates headers to HTTP Request with the format 'key:value'.");

    }
    private static void printHelpPost (){
        Formatter formatter = new Formatter(System.out);
        System.out.println(
                "usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n\n" +
                        "Post executes a HTTP POST request for a given URL with inline data or from file.\n");
        formatter.format("\t%-20s %-200s\n", "-v", "Prints the detail of the response such as protocol, status, and headers.");
        formatter.format("\t%-20s %-200s\n", "-h key:value", "Associates headers to HTTP Request with the format 'key:value'.");
        formatter.format("\t%-20s %-200s\n", "-d string", "Associates an inline data to the body HTTP POST request.");
        formatter.format("\t%-20s %-200s\n", "-f file", "Associates the content of a file to the body HTTP POST request.");
        System.out.println("Either [-d] or [-f] can be used but not both.");
    }
}

