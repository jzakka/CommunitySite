package CommunitySIte.demo.exception;

public class ObjectNotExistsException extends Exception {
    public ObjectNotExistsException() {
        super();
    }

    public ObjectNotExistsException(String message) {
        super(message);
    }

    public ObjectNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectNotExistsException(Throwable cause) {
        super(cause);
    }

    protected ObjectNotExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
