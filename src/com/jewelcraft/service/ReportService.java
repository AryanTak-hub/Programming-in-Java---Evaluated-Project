package com.jewelcraft.service;

import com.jewelcraft.model.Bill;
import com.jewelcraft.model.BillItem;
import com.jewelcraft.model.JewelryItem;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MODULE 3: Reports & Analytics.
 * Derives sales totals, best-sellers and low-stock alerts purely from data
 * already held by the other two modules — no separate storage of its own.
 */
public class ReportService {

    private final BillingService billingService;
    private final InventoryService inventoryService;

    public ReportService(BillingService billingService, InventoryService inventoryService) {
        this.billingService = billingService;
        this.inventoryService = inventoryService;
    }

    public static class SalesSummary {
        public int billCount;
        public double totalRevenue;
        public double totalGstCollected;
    }

    public SalesSummary salesSummary(LocalDateTime from, LocalDateTime to) {
        SalesSummary summary = new SalesSummary();
        for (Bill b : billingService.allBills()) {
            if (!b.getDateTime().isBefore(from) && !b.getDateTime().isAfter(to)) {
                summary.billCount++;
                summary.totalRevenue += b.getTotalAmount();
                summary.totalGstCollected += b.getGstAmount();
            }
        }
        return summary;
    }

    /** Top N best-selling items by total quantity sold, across all bills. */
    public List<Map.Entry<String, Integer>> topSellingItems(int n) {
        Map<String, Integer> qtyByName = new HashMap<>();
        for (Bill b : billingService.allBills()) {
            for (BillItem line : b.getItems()) {
                qtyByName.merge(line.getItemName(), line.getQuantity(), Integer::sum);
            }
        }
        return qtyByName.entrySet().stream()
                .sorted((a, c) -> c.getValue() - a.getValue())
                .limit(n)
                .collect(Collectors.toList());
    }

    public List<JewelryItem> lowStockAlerts(int threshold) {
        return inventoryService.lowStockItems(threshold);
    }
}
