import assignment1.common.HeaderKey;
import org.junit.Test;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-09-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class EnumTest {

    @Test
    public void test() {
        String name = "ALLOW";
        HeaderKey key = HeaderKey.valueOf(name);
        System.out.println(key.name);

    }
}
