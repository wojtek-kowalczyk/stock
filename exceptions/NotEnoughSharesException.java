package exceptions;

public class NotEnoughSharesException extends Exception {
    public final int requested;
    public final int available;

    public NotEnoughSharesException(String message, int requested, int available) {
        super(message);
        this.requested = requested;
        this.available = available;
    }
}
