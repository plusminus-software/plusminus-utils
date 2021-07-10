package company.plusminus.util.exception;

public class ConstructionException extends RuntimeException {

    public ConstructionException(String message) {
        super(message);
    }

    public ConstructionException(Exception exception) {
        super(exception);
    }
}
