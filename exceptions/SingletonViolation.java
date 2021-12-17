package exceptions;

public class SingletonViolation extends Exception {
    public SingletonViolation(String msg) {
        super(msg);
    }
}
