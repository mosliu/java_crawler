package net.liuxuan.crawler.spring.runner.worker;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2019-12-12
 **/
public class NoArgsException extends RuntimeException {
    public NoArgsException() {
    }

    public NoArgsException(String message) {
        super(message);
    }

    public NoArgsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoArgsException(Throwable cause) {
        super(cause);
    }

    public NoArgsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
