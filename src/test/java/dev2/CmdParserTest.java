package dev2;

import assignment2.cmd.CmdParser;
import assignment2.common.ParamHolder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-10-21
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class CmdParserTest {

    @Test
    public void test() {
        String[]    args   = {"httpfs", "-v"};
        CmdParser   parser = new CmdParser(args);
        ParamHolder holder = parser.getParamHolder();
        Assert.assertTrue(holder.isVerbose);
        Assert.assertEquals(8080, holder.portNumber);
        Assert.assertTrue(!holder.hasDirectory);
        Assert.assertEquals("/Users/ERIC_LAI/IdeaProjects/COMP6461", holder.directory);
    }

    @Test
    public void test1() {
        String[]    args   = {"httpfs", "-v", "-p", "8001"};
        CmdParser   parser = new CmdParser(args);
        ParamHolder holder = parser.getParamHolder();
        Assert.assertTrue(holder.isVerbose);
        Assert.assertEquals(8001, holder.portNumber);
        Assert.assertTrue(!holder.hasDirectory);
        Assert.assertEquals("/Users/ERIC_LAI/IdeaProjects/COMP6461", holder.directory);
    }

    @Test
    public void test2() {
        String[]    args   = {"httpfs", "-v", "-p", "8001", "-d", "/Users/ERIC_LAI/IdeaProjects/"};
        CmdParser   parser = new CmdParser(args);
        ParamHolder holder = parser.getParamHolder();
        Assert.assertTrue(holder.isVerbose);
        Assert.assertEquals(8001, holder.portNumber);
        Assert.assertTrue(holder.hasDirectory);
        Assert.assertEquals("/Users/ERIC_LAI/IdeaProjects/", holder.directory);
    }
}
