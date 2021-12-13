package exceptions;

public class UnhandledOrderTypeException extends Exception {
    public UnhandledOrderTypeException(String message) {
        super(message);
    }
}
