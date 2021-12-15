import java.util.TreeMap;
import javax.swing.JFrame;
import exceptions.InsufficientFundsException;

public class User implements Runnable {
    public final String username;
    public TreeMap<Asset, Integer> ownedAssets; // <asset, quantity>
    private float balance;

    public User(String username, float initialBalance) {
        this.username = username;
        this.balance = (initialBalance < 0 ? 0f : initialBalance);
    }

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

    @Override
    public void run() {
        JFrame userWindow = new JFrame(username);
        userWindow.setSize(600, 400);
        userWindow.setVisible(true);
        System.out.println(username + " session is running on it's own thread.");
    }
}
