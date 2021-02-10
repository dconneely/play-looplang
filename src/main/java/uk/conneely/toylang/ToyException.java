package uk.conneely.toylang;

public class ToyException extends RuntimeException {
    public ToyException(String message) {
        super(message);
    }

    public ToyException(String message, Throwable cause) {
        super(message, cause);
    }
}
