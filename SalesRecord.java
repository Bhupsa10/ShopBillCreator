package modules;

public class SalesRecord {
    private final Item item;
    private int totalQuantity;

    public SalesRecord(Item item) {
        this.item = item;
        this.totalQuantity = 0;
    }

    public void addQuantity(int qty) {
        totalQuantity += qty;
    }

    public int getTotalQuantity() { return totalQuantity; }

    public double getTotalPrice() {
        return item.getPrice() * totalQuantity;
    }

    public Item getItem() { return item; }
}
