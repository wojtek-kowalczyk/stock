
// import java.util.Scanner;
import java.util.TreeMap;

import exceptions.InsufficientFundsException;

public class User {
    public final String username;
    public TreeMap<Asset, Integer> ownedAssets;
    private float balance;
    // private boolean stop = false;

    public User(String username, float initialBalance) {
        this.username = username;
        this.balance = (initialBalance < 0 ? 0f : initialBalance);
    }

    // @Override
    // public void run() {
    // System.out.println("Running user " + username);
    // StartEventLoop();
    // }

    @Override
    public String toString() {
        return "USER: " + username + ", balance: " + balance;
    }

    // if the exception is thrown the balance is unchanged
    public void changeBalance(float diff) throws InsufficientFundsException {
        if (balance + diff < 0)
            throw new InsufficientFundsException(Math.abs(diff), balance);
        balance += diff;
    }

    // public void StartEventLoop() {
    // Scanner sc = new Scanner(System.in);
    // while (!stop) {
    // System.out.println("Active User: " + username + " | INPUT: ");
    // switch (sc.nextLine().trim()) {
    // case "exit":
    // StopEventLoop();
    // break;
    // case "echo":
    // System.out.println("DUPA DUPA DUPA DUPA");
    // break;

    // default:
    // break;
    // }
    // }
    // sc.close();
    // }

    // public void StopEventLoop() {
    // stop = true;
    // }
}
