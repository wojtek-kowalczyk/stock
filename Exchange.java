import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

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

    private ArrayList<Asset> assets;
    private ArrayList<Order> orders; // earlier orders have lower index

    public boolean stop = false; // todo - consider a better implementation

    public Exchange() {
        assets = new ArrayList<Asset>();
        orders = new ArrayList<Order>();
    }

    public void init() {
        createAssets();
        User dummy = new User("dummy-user", 100000000);
        try {
            placeOrder(new Order(assets.get(0), OrderType.BUY, true, 10, 10, dummy));
            placeOrder(new Order(assets.get(1), OrderType.BUY, true, 10, 10, dummy));
            placeOrder(new Order(assets.get(2), OrderType.BUY, true, 10, 10, dummy));
            placeOrder(new Order(assets.get(2), OrderType.BUY, true, 5, 10000, dummy));
            placeOrder(new Order(assets.get(3), OrderType.BUY, true, 10, 10, dummy));
            placeOrder(new Order(assets.get(4), OrderType.BUY, true, 10, 10, dummy));

            // System.out.println("All orders: ");
            // for (Asset asset : assets) {

            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (!stop) {
            try {
                executeTasks();
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                System.out.println("Exception in exchange main loop");
                e.printStackTrace();
                stop = true;
            }
        }
    }

    public List<Asset> getAssets() {
        return assets;
    }

    private void executeTasks() {
        System.out.println("Executing tasks");
        // change the price
        for (Asset asset : assets) {
            asset.price = getNewPrice(asset.price, MIN_PRICE_CHANGE, MAX_PRICE_CHANGE);
        }
        // DEBUG
        System.out.println("Assets:");
        for (Asset asset : assets) {
            System.out.println(asset.toString());
        }
        System.out.println("Orders:");
        for (Order order : orders) {
            System.out.println(order.toString());
        }
        // END DEBUG
        // after the price is changed refresh orders to see if some can be completed
        while (checkOrders()) {
            // ? is this a good idea? seems like a workaround
            // this will continue checking untill no order is able to be executed
            // this is because when order in the middle of the list is executed it triggers
            // a price change, and orders should be checked again, from the begining
        }
        System.out.println("Orders After checking :");
        for (Order order : orders) {
            System.out.println(order.toString());
        }

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
        while (checkOrders()) {
        }
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
                            System.out.println("executing buy order. order price: " + order.price + ", asset price: "
                                    + order.asset.price);
                            executeOrder(order);
                            return true;
                        }
                    case SELL:
                        if (order.price <= order.asset.price) {
                            System.out.println("executing sell order. order price: " + order.price + ", asset price: "
                                    + order.asset.price);
                            executeOrder(order);
                            return true;
                        }
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
        System.out.println("Successfully executed order " + order.toString());
    }

}
