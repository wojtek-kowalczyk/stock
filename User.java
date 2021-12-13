import java.util.TreeMap;
import exceptions.InsufficientFundsException;

public class User {
    public final String username;
    public TreeMap<Asset, Integer> ownedAssets;
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
}
