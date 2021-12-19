public class Asset {
    private final String name;
    private final int totalShares;

    private int availableShares;
    public float price;

    public Asset(String name, int shares, float initPrice) {
        this.name = name;
        this.totalShares = shares;
        this.availableShares = totalShares;
        this.price = initPrice;
    }

    @Override
    public String toString() {
        return name + ":\t$" + price + "\t| total shares: " + totalShares;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public int getAvailableShares() {
        return availableShares;
    }

    public void changeShares(int diff) {
        if (availableShares + diff < 0)
            // ? is this a good idea? or maybe use assert?
            throw new RuntimeException("Trying to set availableShares to a negative value. This should never happen.");
        availableShares += diff;
    }

    public void priceReaction(boolean isBuy, int quantity) {
        // 10% assets sold = 10% price change
        float percentage = ((float) quantity / this.totalShares);
        this.price = this.price * (1 + (isBuy ? percentage : -percentage));
    }

    public String getName() {
        return name;
    }

}