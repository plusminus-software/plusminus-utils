package company.plusminus.util.exception;

public class LoadException extends RuntimeException {

    public LoadException(String message) {
        super(message);
    }

    public LoadException(Exception exception) {
        super(exception);
    }
}
