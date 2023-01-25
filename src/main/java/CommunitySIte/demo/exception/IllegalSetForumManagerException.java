package CommunitySIte.demo.exception;

public class IllegalSetForumManagerException extends Exception {
    public IllegalSetForumManagerException() {
        super();
    }

    public IllegalSetForumManagerException(String message) {
        super(message);
    }

    public IllegalSetForumManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalSetForumManagerException(Throwable cause) {
        super(cause);
    }

    protected IllegalSetForumManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
