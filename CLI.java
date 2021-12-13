public class CLI {
    public static void main(String[] args) {
        UserManager um = new UserManager();
        Thread userManagerThread = new Thread(um, "UMThread");
        userManagerThread.start();

        Exchange ex = new Exchange();
        ex.init();
    }
}
