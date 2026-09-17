package com.jewelcraft.model;

/**
 * Represents a single jewellery item held in inventory.
 * Price is computed dynamically from live metal rate + making charge,
 * so updating the market rate re-prices the whole catalogue automatically.
 */
public class JewelryItem {

    private String itemId;
    private String name;
    private String category;      // Ring, Necklace, Earrings, Bracelet, Chain
    private String metalType;     // Gold, Silver, Platinum
    private String purity;        // e.g. 22K, 24K, 925 Silver
    private double weightInGrams;
    private double ratePerGram;         // current market rate for that metal/purity
    private double makingChargePerGram; // artisan/labour charge
    private int stockQuantity;

    public JewelryItem(String itemId, String name, String category, String metalType,
                        String purity, double weightInGrams, double ratePerGram,
                        double makingChargePerGram, int stockQuantity) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.metalType = metalType;
        this.purity = purity;
        this.weightInGrams = weightInGrams;
        this.ratePerGram = ratePerGram;
        this.makingChargePerGram = makingChargePerGram;
        this.stockQuantity = stockQuantity;
    }

    /** Base price of ONE unit before GST: (metal value) + (making charge). */
    public double calculateUnitPrice() {
        double metalValue = weightInGrams * ratePerGram;
        double makingCharge = weightInGrams * makingChargePerGram;
        return metalValue + makingCharge;
    }

    // --- Getters & Setters ---
    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getMetalType() { return metalType; }
    public void setMetalType(String metalType) { this.metalType = metalType; }
    public String getPurity() { return purity; }
    public void setPurity(String purity) { this.purity = purity; }
    public double getWeightInGrams() { return weightInGrams; }
    public void setWeightInGrams(double weightInGrams) { this.weightInGrams = weightInGrams; }
    public double getRatePerGram() { return ratePerGram; }
    public void setRatePerGram(double ratePerGram) { this.ratePerGram = ratePerGram; }
    public double getMakingChargePerGram() { return makingChargePerGram; }
    public void setMakingChargePerGram(double makingChargePerGram) { this.makingChargePerGram = makingChargePerGram; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    /** Serialise to a single CSV line for file persistence. */
    public String toCSV() {
        return String.join(",", itemId, name, category, metalType, purity,
                String.valueOf(weightInGrams), String.valueOf(ratePerGram),
                String.valueOf(makingChargePerGram), String.valueOf(stockQuantity));
    }

    /** Rebuild a JewelryItem from a CSV line written by toCSV(). */
    public static JewelryItem fromCSV(String line) {
        String[] p = line.split(",", -1);
        return new JewelryItem(p[0], p[1], p[2], p[3], p[4],
                Double.parseDouble(p[5]), Double.parseDouble(p[6]),
                Double.parseDouble(p[7]), Integer.parseInt(p[8]));
    }

    @Override
    public String toString() {
        return String.format("%-8s %-18s %-10s %-8s %-8s %7.2fg  Rs.%-10.2f  Stock:%d",
                itemId, name, category, metalType, purity, weightInGrams,
                calculateUnitPrice(), stockQuantity);
    }
}
