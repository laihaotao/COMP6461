package assignment3;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public interface TimerCallback {

    void handler(boolean recvDataAck, Packet packet);
}
