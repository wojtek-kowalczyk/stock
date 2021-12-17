import exceptions.SingletonViolation;

// todo - consider moving the main function to exchange or think how this class can handle GUI

public class GUI {
    public static void main(String[] args) {
        try {
            Exchange ex = new Exchange();
            ex.init();
        } catch (SingletonViolation e) {
            System.out.println(e.getMessage());
        }
    }
}
