import exceptions.SingletonViolation;

public class CLI {
    public static void main(String[] args) {
        try {
            Exchange ex = new Exchange();
            ex.init();
        } catch (SingletonViolation e) {
            System.out.println(e.getMessage());
        }
    }
}
