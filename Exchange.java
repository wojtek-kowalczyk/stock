import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

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
import java.util.ArrayList;
import java.util.List;

public class Exchange {

    private final int MIN_ASSETS = 5;
    private final int MAX_ASSETS = 10;
    private final float MIN_ASSET_PRICE = 1.0f;
    private final float MAX_ASSET_PRICE = 10_000.0f;
    private final float MIN_ASSET_PRICE_INITIAL = 1.0f;
    private final float MAX_ASSET_PRICE_INITIAL = 1_000.0f;
    private final float MIN_PRICE_CHANGE = 1.0f;
    private final float MAX_PRICE_CHANGE = 3.0f;
    private final int MIN_NUM_SHARES = 20;
    private final int MAX_NUM_SHARES = 2000;
    private final int SLEEP_TIME = 5000; // milliseconds

    private final String ASSETS_DATA_FILENAME = "Assets.txt";

    // GUI elements
    private final int PADDING = 50;
    private final int WIDTH = 1200;
    private final int HEIGHT = 900;

    private JFrame exchangeWindow;
    private JTextArea assetsArea;
    private JScrollPane assetScrollPane;
    private JTextArea consoleArea;
    private JScrollPane consoleScrollPane;
    private JTextArea ordersArea;
    private JScrollPane ordersScrollPane;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JButton launchUserButton;

    private ArrayList<Asset> assets;
    private ArrayList<Order> orders; // earlier orders have lower index

    private boolean stop = false; // todo - consider a better implementation

    public Exchange() {
        assets = new ArrayList<Asset>();
        orders = new ArrayList<Order>();
    }

    public void init() {
        System.out.println("creating assets");
        createAssets();
        setupGUI();

        // #region debug
        System.out.println("creating user and placing orders");
        User dummy = new User("dummy-user", 1_000_000);
        // create user thread
        // Thread userThread = new Thread(dummy, "user");
        // userThread.start();

        try {
            placeOrder(new Order(assets.get(0), OrderType.BUY, true, 10, 10, dummy));
            placeOrder(new Order(assets.get(1), OrderType.BUY, true, 10, 10, dummy));
            placeOrder(new Order(assets.get(2), OrderType.BUY, true, 10, 10, dummy));
            placeOrder(new Order(assets.get(2), OrderType.BUY, true, 5, 10_000, dummy));
            placeOrder(new Order(assets.get(3), OrderType.BUY, true, 10, 10, dummy));
            placeOrder(new Order(assets.get(4), OrderType.BUY, true, 10, 10, dummy));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // #endregion

        while (!stop) {
            try {
                executeTasks();
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                System.out.println("Exception in exchange main loop");
                e.printStackTrace();
                halt();
            }
        }
    }

    public void halt() {
        stop = true;
    }

    // ? is there a point in this? it returns a reference = can be modified
    public List<Asset> getAssets() {
        return assets;
    }

    private void executeTasks() {
        System.out.println("Executing tasks");
        for (Asset asset : assets) {
            asset.price = getNewPrice(asset.price, MIN_PRICE_CHANGE, MAX_PRICE_CHANGE);
        }

        // output to GUI
        String assetsString = "Assets:\n";
        for (Asset asset : assets) {
            assetsString += asset.toString() + "\n";
        }
        assetsArea.setText(assetsString);
        String ordersString = "Orders:\n";
        for (Order order : orders) {
            ordersString += order.toString() + "\n";
        }
        ordersArea.setText(ordersString);

        while (checkOrders()) {
            // ? is this a good idea? seems like a workaround
            // this will continue checking untill no order is able to be executed
            // this is because when order in the middle of the list is executed it triggers
            // a price change, and orders should be checked again, from the begining
        }

        // #region debug
        // System.out.println("Orders After checking :");
        // for (Order order : orders) {
        // System.out.println(order.toString());
        // }
        // #endregion

    }

    private float getNewPrice(float original, float minChange, float maxChange) {
        Random rand = new Random();
        // y = a*x + b, where b is the lower out limit, a is (upper-lower)
        float changeAbs = (maxChange - minChange) * rand.nextFloat() + minChange;

        float newPrice = ((rand.nextFloat() < 0.5f) ? (original + changeAbs) : (original - changeAbs));
        if (newPrice > MAX_ASSET_PRICE)
            newPrice = MAX_ASSET_PRICE;
        else if (newPrice < MIN_ASSET_PRICE)
            newPrice = MIN_ASSET_PRICE;
        return newPrice;
    }

    public void placeOrder(Order order) throws NotEnoughSharesException, InsufficientFundsException {
        if (order.quantity > order.asset.getTotalShares())
            throw new NotEnoughSharesException("not enough shares", order.quantity, order.asset.getTotalShares());
        order.user.changeBalance(-order.quantity * order.price); // negative change = subtract
        orders.add(order);
        // temp ! commented out for debugging
        // while (checkOrders()) {
        // }
    }

    private void createAssets() {
        Random rand = new Random();
        ArrayList<Asset> allAssets = new ArrayList<Asset>();
        loadAssets(allAssets);
        for (int i = 0; i < rand.nextInt(MAX_ASSETS + 1 + MIN_ASSETS) + MIN_ASSETS; i++) {
            assets.add(allAssets.remove(rand.nextInt(allAssets.size())));
        }
    }

    private void loadAssets(List<Asset> loadTo) {
        Scanner sc;
        Random rand = new Random();
        File file = new File(ASSETS_DATA_FILENAME);
        try {
            sc = new Scanner(file);
            while (sc.hasNextLine()) {
                // create asset
                loadTo.add(new Asset(sc.nextLine().trim(),
                        rand.nextInt(MAX_NUM_SHARES + 1 + MIN_NUM_SHARES) + MIN_NUM_SHARES,
                        // map 0-1 to A-B -> y=(B-A)*x+A, where A=min, B=max, x=input
                        (MAX_ASSET_PRICE_INITIAL - MIN_ASSET_PRICE_INITIAL) * rand.nextFloat()
                                + MIN_ASSET_PRICE_INITIAL));
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File wasn't found");
        }
    }

    // * order execution scheme
    // loop through orders in order of placement.
    // if current order can be executed, execute it. This will lower or add shares
    // then check the next one, but now consider that although order was placed,
    // there might be not enough shares to fullfill that order (previous executed)
    // if that's the case -> execute for as much shares as it's possible
    // and then leave the order, while informing the user that their order was
    // processed partially,
    // and awaits further processing.
    // ? add this Partial Processinng as an option?
    // ? then if true, do what I said above, otherwise maket he order wait.

    // returns false if no orders could be executed
    public boolean checkOrders() {
        System.out.println("checking orders");
        try {
            for (Order order : orders) {
                switch (order.type) {
                    case BUY:
                        if (order.price >= order.asset.price) {
                            consoleArea
                                    .append("executing buy order. order price: " + order.price + ", asset price: "
                                            + order.asset.price + "\n");
                            executeOrder(order);
                            return true;
                        }
                        break;
                    case SELL:
                        if (order.price <= order.asset.price) {
                            consoleArea.append("executing sell order. order price: " + order.price + ", asset price: "
                                    + order.asset.price + "\n");
                            executeOrder(order);
                            return true;
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (UnhandledOrderTypeException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void executeOrder(Order order) throws UnhandledOrderTypeException {
        System.out.println("executing order");
        try {
            switch (order.type) {
                case BUY:
                    // user already paid when placing the order
                    order.asset.changeShares(-order.quantity);
                    break;
                case SELL:
                    order.user.changeBalance(+order.quantity * order.price);
                    order.asset.changeShares(+order.quantity);
                    break;

                default:
                    throw new UnhandledOrderTypeException("Add a switch case for type: " + order.type);
            }
            orders.remove(order);
            order.asset.priceReaction(order.type == OrderType.BUY, order.quantity);

        } catch (InsufficientFundsException e) {
            // this will never happen when adding funds, and this function doesn't subtract
            // they are subtracted when placing the order
            System.out.println(
                    "InsufficientFundsException thrown when adding funds. This is impossible. Check the method for changing user's balance");
        }
        consoleArea.append("Successfully executed order " + order.toString() + "\n");
    }

    private void setupGUI() {
        // GUI
        exchangeWindow = new JFrame("Exchange");

        // OUTPUT FIELDS
        assetsArea = new JTextArea("");
        assetScrollPane = new JScrollPane(assetsArea);
        assetScrollPane.setBounds(PADDING, PADDING, WIDTH - 2 * PADDING, (HEIGHT - 2 * PADDING) / 2);
        assetsArea.setEditable(false);
        exchangeWindow.add(assetScrollPane);

        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleScrollPane = new JScrollPane(consoleArea);
        consoleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        consoleScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        consoleScrollPane.setBounds(
                PADDING,
                PADDING + (HEIGHT - 2 * PADDING) / 2 + PADDING / 2,
                (WIDTH - 2 * PADDING) / 2 - PADDING / 2,
                (HEIGHT - 3 * PADDING) / 2);
        exchangeWindow.add(consoleScrollPane);

        ordersArea = new JTextArea("");
        ordersArea.setEditable(false);
        ordersScrollPane = new JScrollPane(ordersArea);
        ordersScrollPane.setBounds(
                PADDING + (WIDTH - 2 * PADDING) / 2 - PADDING / 2 + PADDING / 2,
                PADDING + (HEIGHT - 2 * PADDING) / 2 + PADDING / 2,
                (WIDTH - 2 * PADDING) / 2,
                (HEIGHT - 3 * PADDING) / 2);
        exchangeWindow.add(ordersScrollPane);

        // USER LOGIN AREA
        usernameLabel = new JLabel("username:");
        usernameLabel.setBounds(PADDING / 2, 0, 100, 50);
        exchangeWindow.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(PADDING / 2 + 100, 25 / 2, 100, 25);
        exchangeWindow.add(usernameField);

        launchUserButton = new JButton("Launch User");
        launchUserButton.setBounds(PADDING / 2 + 100 + 100, 25 / 2, 175, 25);
        launchUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                consoleArea.append("Launching user with username " + usernameField.getText() + "\n");
                Thread t = new Thread(new User(usernameField.getText(), User.DEAFULT_BALANCE));
                t.start();
            }
        });
        exchangeWindow.add(launchUserButton);

        // FINAL WINDOW SETUP
        exchangeWindow.setSize(WIDTH, HEIGHT);
        exchangeWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        exchangeWindow.setLayout(null);
        exchangeWindow.setVisible(true);
    }
}
