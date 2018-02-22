package by.zinovich.javastudy.exceptions;

public class MyServletException extends Exception {

    public MyServletException() {
    }

    public MyServletException(String message) {
        super(message);
    }

    public MyServletException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyServletException(Throwable cause) {
        super(cause);
    }

    public MyServletException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
