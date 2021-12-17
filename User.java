import java.util.HashMap;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.*;

import exceptions.InsufficientFundsException;
import exceptions.NotEnoughSharesException;
import exceptions.UnhandledOrderTypeException;

// todo - make it so that when during one session you close and launch the same user(name) it launches the same user

public class User implements Runnable {
    public static final float DEAFULT_BALANCE = 100_000f;
    public final String username;
    private HashMap<Asset, Integer> ownedAssets; // <asset, quantity>
    private float balance;

    // GUI elements
    JFrame userWindow;
    JLabel assetNameLabel;
    JLabel assetQuantityLabel;
    JLabel assetPriceLabel;
    JLabel userDataLabel;
    JTextField assetNameField;
    JTextField assetQuantityField;
    JTextField assetPriceField;
    JTextArea userDataArea;
    JScrollPane userDataScrollPane;
    JButton buyButton;
    JButton sellButton;

    public User(String username, float initialBalance) {
        this.username = username;
        this.balance = (initialBalance < 0 ? 0f : initialBalance);
        this.ownedAssets = new HashMap<Asset, Integer>();
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
        setUserDataText(); // refresh
    }

    @Override
    public void run() {
        System.out.println(username + " session is running on it's own thread.");
        setupGUI();
    }

    public void changeOwnedAmount(Asset asset, int quantity) {
        if (ownedAssets.containsKey(asset)) {
            // modify the amount owned
            ownedAssets.put(asset, ownedAssets.get(asset) + quantity);
            // * run with -ea to enable assertions.
            assert ownedAssets.get(asset) >= 0 : "User: " + username + " had: " +
                    ownedAssets.get(asset)
                    + " shares for asset: " + asset.getName()
                    + ". This is impossible. Trace the asset through logs.";
        } else {
            // put it the first time
            ownedAssets.put(asset, quantity);
        }
        setUserDataText(); // refresh
    }

    public int getOwnedAmount(Asset asset) {
        return ownedAssets.get(asset) == null ? 0 : ownedAssets.get(asset);
    }

    private void setUserDataText() {
        userDataArea.setText("username: " + username + "\nbalance: " + balance + "\nowned assets:\n");
        for (Asset asset : ownedAssets.keySet()) {
            String assetStr = asset.getName() + "\t owned: " + ownedAssets.get(asset) + "\n";
            userDataArea.append(assetStr);
        }
    }

    private void setupGUI() {
        // ! BAD CODE WARNING

        userWindow = new JFrame(username);
        assetNameField = new JTextField();
        assetQuantityField = new JTextField();
        assetPriceField = new JTextField();
        assetNameLabel = new JLabel("asset name:");
        assetQuantityLabel = new JLabel("quantity:");
        assetPriceLabel = new JLabel("price:");
        buyButton = new JButton("BUY");
        sellButton = new JButton("SELL");
        userDataLabel = new JLabel("user data:");
        userDataArea = new JTextArea();
        userDataScrollPane = new JScrollPane(userDataArea);
        userDataScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        userDataArea.setEditable(false);
        // userDataArea.setText("username: " + username + "\nbalance: " + balance +
        // "\nowned assets:\n");
        // for (Asset asset : ownedAssets.keySet()) {
        // String assetStr = asset.getName() + "\t owned: " + ownedAssets.get(asset) +
        // "\n";
        // userDataArea.append(assetStr);
        // }
        setUserDataText();

        // ? is there a way not to duplicate this code (only difference is the type),
        // ? but still use an anonymous object?
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Asset a = Exchange.instance.getAssetByName(assetNameField.getText());
                    if (a == null) {
                        Exchange.instance.logGUI("Asset with that name cannot be found.");
                        return;
                    }
                    Order o = new Order(
                            a,
                            OrderType.BUY,
                            true,
                            Integer.parseInt(assetQuantityField.getText()),
                            Float.parseFloat(assetPriceField.getText()),
                            User.this);
                    Exchange.instance.placeOrder(o);
                } catch (NotEnoughSharesException | InsufficientFundsException ex) {
                    Exchange.instance.logGUI(ex.getMessage());
                } catch (NumberFormatException ex) {
                    System.out.println("order can't be placed, invalid number format");
                } catch (UnhandledOrderTypeException ex) {
                    System.out.println(ex.getMessage());
                    System.exit(0); // abort
                }
            }
        });
        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Asset a = Exchange.instance.getAssetByName(assetNameField.getText());
                    if (a == null) {
                        Exchange.instance.logGUI("Asset with that name cannot be found.");
                        return;
                    }
                    Order o = new Order(
                            a,
                            OrderType.SELL,
                            true,
                            Integer.parseInt(assetQuantityField.getText()),
                            Float.parseFloat(assetPriceField.getText()),
                            User.this);
                    Exchange.instance.placeOrder(o);
                } catch (NotEnoughSharesException | InsufficientFundsException ex) {
                    Exchange.instance.logGUI(ex.getMessage());
                } catch (NumberFormatException ex) {
                    System.out.println("order can't be placed, invalid number format");
                } catch (UnhandledOrderTypeException ex) {
                    System.out.println(ex.getMessage());
                    System.exit(0); // abort
                }
            }
        });

        userWindow.add(userDataLabel);
        userWindow.add(userDataScrollPane);
        userWindow.add(assetNameLabel);
        userWindow.add(assetNameField);
        userWindow.add(assetQuantityLabel);
        userWindow.add(assetQuantityField);
        userWindow.add(assetPriceLabel);
        userWindow.add(assetPriceField);
        userWindow.add(buyButton);
        userWindow.add(sellButton);

        userWindow.setLayout(new GridLayout(5, 2));
        userWindow.setSize(600, 400);
        userWindow.setVisible(true);

    }
}
