package exceptions;

// ? is it a good idea to allow exceptions without custom message for such specific cases?

public class InsufficientFundsException extends Exception {
    public float required;
    public float available;

    public InsufficientFundsException(float required, float available) {
        super("Insufficient funds. operation requires: " + required + ", available: " + available);
    }
}
