package com.jewelcraft.model;

/** One line of a Bill: an item, the quantity sold, and the price locked in at sale time. */
public class BillItem {

    private String itemId;
    private String itemName;
    private int quantity;
    private double priceAtSale; // per-unit price when the sale happened (rates may change later)

    public BillItem(String itemId, String itemName, int quantity, double priceAtSale) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
    }

    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getPriceAtSale() { return priceAtSale; }
    public double getLineTotal() { return quantity * priceAtSale; }

    public String toCSV() {
        return String.join("|", itemId, itemName, String.valueOf(quantity), String.valueOf(priceAtSale));
    }

    public static BillItem fromCSV(String token) {
        String[] p = token.split("\\|", -1);
        return new BillItem(p[0], p[1], Integer.parseInt(p[2]), Double.parseDouble(p[3]));
    }

    @Override
    public String toString() {
        return String.format("%-18s x%-3d  Rs.%9.2f", itemName, quantity, getLineTotal());
    }
}
