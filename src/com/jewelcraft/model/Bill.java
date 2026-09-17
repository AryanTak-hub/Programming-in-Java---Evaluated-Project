package com.jewelcraft.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Represents a completed invoice: customer, line items, GST and final total. */
public class Bill {

    public static final double GST_RATE = 0.03; // 3% GST, standard for jewellery in India

    private String billId;
    private String customerId;
    private LocalDateTime dateTime;
    private List<BillItem> items;
    private double discountPercent;

    public Bill(String billId, String customerId, LocalDateTime dateTime,
                List<BillItem> items, double discountPercent) {
        this.billId = billId;
        this.customerId = customerId;
        this.dateTime = dateTime;
        this.items = items;
        this.discountPercent = discountPercent;
    }

    public String getBillId() { return billId; }
    public String getCustomerId() { return customerId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public List<BillItem> getItems() { return items; }
    public double getDiscountPercent() { return discountPercent; }

    public double getSubtotal() {
        return items.stream().mapToDouble(BillItem::getLineTotal).sum();
    }

    public double getDiscountAmount() {
        return getSubtotal() * (discountPercent / 100.0);
    }

    public double getGstAmount() {
        return (getSubtotal() - getDiscountAmount()) * GST_RATE;
    }

    public double getTotalAmount() {
        return getSubtotal() - getDiscountAmount() + getGstAmount();
    }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** One CSV line per bill: header fields, then all bill items packed with ';' + '|'. */
    public String toCSV() {
        StringBuilder itemsPacked = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) itemsPacked.append(";");
            itemsPacked.append(items.get(i).toCSV());
        }
        return String.join(",", billId, customerId, dateTime.format(FMT),
                String.valueOf(discountPercent), itemsPacked.toString());
    }

    public static Bill fromCSV(String line) {
        String[] p = line.split(",", 5);
        List<BillItem> items = new ArrayList<>();
        if (!p[4].isEmpty()) {
            for (String tok : p[4].split(";")) {
                items.add(BillItem.fromCSV(tok));
            }
        }
        return new Bill(p[0], p[1], LocalDateTime.parse(p[2], FMT), items, Double.parseDouble(p[3]));
    }
}
