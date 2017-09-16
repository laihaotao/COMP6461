import org.junit.Test;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-10
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class CRLF_Test {

    @Test
    public void test1() {
        System.out.println("================");
        System.out.println("line 1");
        System.out.println("\r\n");
        System.out.println("line 2");
    }

    @Test
    public void test2() {
        System.out.println("================");
        System.out.println("line 1");
        System.out.println("\r");
        System.out.println("line 2");
    }
}
