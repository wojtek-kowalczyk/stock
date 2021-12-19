package exceptions;

// ? should I just throw regular exception with message "singleton violation"?
public class SingletonViolation extends Exception {
    public SingletonViolation(String msg) {
        super(msg);
    }
}
