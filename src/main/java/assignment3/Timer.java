package assignment3;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-12
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class Timer implements Runnable {

    private final int duration = 1000;

    @Override
    public void run() {
        // set a timer, and wait for timeout
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
