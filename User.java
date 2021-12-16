import java.util.TreeMap;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import exceptions.InsufficientFundsException;

public class User implements Runnable {
    public static final float DEAFULT_BALANCE = 100_000f;
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
        System.out.println(username + " session is running on it's own thread.");
        setupGUI();
    }

    private void setupGUI() {

        // ! BAD CODE WARNING
        JFrame userWindow;
        JLabel assetNameLabel;
        JLabel assetQuantityLabel;
        JLabel assetPriceLabel;
        JTextField assetNameField;
        JTextField assetQuantityField;
        JTextField assetPriceField;
        JButton buyButton;
        JButton sellButton;

        userWindow = new JFrame(username);
        assetNameField = new JTextField();
        assetQuantityField = new JTextField();
        assetPriceField = new JTextField();
        assetNameLabel = new JLabel("asset name:");
        assetQuantityLabel = new JLabel("quantity:");
        assetPriceLabel = new JLabel("price:");
        buyButton = new JButton("BUY");
        sellButton = new JButton("SELL");

        userWindow.add(assetNameLabel);
        userWindow.add(assetNameField);
        userWindow.add(assetQuantityLabel);
        userWindow.add(assetQuantityField);
        userWindow.add(assetPriceLabel);
        userWindow.add(assetPriceField);
        userWindow.add(buyButton);
        userWindow.add(sellButton);

        userWindow.setLayout(new GridLayout(4, 2));
        userWindow.setSize(600, 400);
        userWindow.setVisible(true);

    }
}
