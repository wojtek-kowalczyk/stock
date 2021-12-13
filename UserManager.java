import java.util.ArrayList;
import java.util.Scanner;

public class UserManager implements Runnable {
    public static User activeUser;
    public static ArrayList<User> users;

    public UserManager() {
        users = new ArrayList<User>();
        activeUser = null;
    }

    @Override
    public void run() {
        // create users
        users.add(new User("user1", 250000));
        users.add(new User("user2", 250000));
        users.add(new User("user3", 250000));

        // user selection loop
        Scanner sc = new Scanner(System.in);
        boolean stop = false;
        String currLine;
        while (!stop) {
            System.out.println("INPUT:");
            currLine = sc.nextLine().trim();
            if (activeUser == null) {
                // ! CAN'T EXIT THE PROGRAM IN THIS STATE. MIND WHEN IMPL. GUI
                System.out.println("Enter Username. \"exit\" to exit the program.");
                for (User user : users) {
                    if (user.username.equals(currLine)) {
                        activeUser = user;
                        break;
                    }
                }
            } else { // activeUser != null
                switch (currLine) {
                    case "exit":
                        stop = true;
                        break;
                    case "echo":
                        System.out.println("TEST STRING. I AM ALIVE!!!!");
                        break;
                    default:
                        break;
                }
            }
        }
        // ! usermanager stops but the exchange is still running.
        sc.close();
    }
}
