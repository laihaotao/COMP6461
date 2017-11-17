package assignment3.exception;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2017-11-16
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class HandShakingFailException extends RuntimeException {

    public HandShakingFailException() {
        this("HandShaking fail, please check your Internet connection.");
    }

    private HandShakingFailException(String message) {
        super(message);
    }
}
