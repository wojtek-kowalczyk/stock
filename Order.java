public class Order {
    public final Asset asset;
    public final OrderType type;
    public final int quantity;
    public final float price;
    public final User user;
    // todo allowPartial has no effect
    public final boolean allowPartial; // allow the order to be partially executed

    public Order(Asset asset, OrderType type, boolean allowPartial, int quantity, float price, User user) {
        this.asset = asset;
        this.type = type;
        this.allowPartial = allowPartial;
        this.quantity = quantity;
        this.price = price;
        this.user = user;
    }

    @Override
    public String toString() {
        return "ORDER: {" + asset.getName() + "} x " + quantity + " | TYPE: " + type + " | AT: $" + price
                + " PER UNIT | TOTAL: $" + quantity * price;
    }
}