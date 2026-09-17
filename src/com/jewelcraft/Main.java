package com.jewelcraft;

import com.jewelcraft.exception.JewelCraftException;
import com.jewelcraft.model.Bill;
import com.jewelcraft.model.Customer;
import com.jewelcraft.model.JewelryItem;
import com.jewelcraft.repository.BillRepository;
import com.jewelcraft.repository.CustomerRepository;
import com.jewelcraft.repository.InventoryRepository;
import com.jewelcraft.service.BillingService;
import com.jewelcraft.service.CustomerService;
import com.jewelcraft.service.InventoryService;
import com.jewelcraft.service.ReportService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * JewelCraft — Inventory & Billing System.
 * A command-line application for a jewellery store: manage stock, raise
 * invoices with automatic GST calculation, and view sales/stock reports.
 * All data persists to plain CSV files under ./data so nothing else needs
 * to be installed to run or evaluate it.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        InventoryService inventoryService = new InventoryService(new InventoryRepository("data"));
        CustomerService customerService = new CustomerService(new CustomerRepository("data"));
        BillingService billingService = new BillingService(new BillRepository("data"), inventoryService, customerService);
        ReportService reportService = new ReportService(billingService, inventoryService);

        seedDemoDataIfEmpty(inventoryService, customerService);

        System.out.println("=========================================");
        System.out.println("   JewelCraft - Inventory & Billing System");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            System.out.println("\nMAIN MENU");
            System.out.println("1. Inventory Management");
            System.out.println("2. Billing");
            System.out.println("3. Reports & Analytics");
            System.out.println("4. Customer Management");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> inventoryMenu(inventoryService);
                    case "2" -> billingMenu(billingService, inventoryService);
                    case "3" -> reportsMenu(reportService);
                    case "4" -> customerMenu(customerService);
                    case "5" -> running = false;
                    default -> System.out.println("Invalid option, please choose 1-5.");
                }
            } catch (JewelCraftException e) {
                // Every domain error is caught here so the app never crashes on bad input.
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    // ---------------- Inventory ----------------

    private static void inventoryMenu(InventoryService svc) throws JewelCraftException {
        System.out.println("\n-- Inventory Management --");
        System.out.println("1. Add item");
        System.out.println("2. List all items");
        System.out.println("3. Search by category");
        System.out.println("4. Search by name");
        System.out.println("5. Update stock");
        System.out.println("6. Update rate per gram");
        System.out.println("7. Delete item");
        System.out.println("8. Low-stock alert");
        System.out.println("9. Back");
        System.out.print("Choose an option: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("Item ID: "); String id = sc.nextLine().trim();
                System.out.print("Name: "); String name = sc.nextLine().trim();
                System.out.print("Category (Ring/Necklace/Earrings/Bracelet/Chain): "); String cat = sc.nextLine().trim();
                System.out.print("Metal (Gold/Silver/Platinum): "); String metal = sc.nextLine().trim();
                System.out.print("Purity (e.g. 22K): "); String purity = sc.nextLine().trim();
                System.out.print("Weight in grams: "); double weight = readDouble();
                System.out.print("Rate per gram (Rs.): "); double rate = readDouble();
                System.out.print("Making charge per gram (Rs.): "); double making = readDouble();
                System.out.print("Stock quantity: "); int stock = readInt();
                JewelryItem item = svc.addItem(id, name, cat, metal, purity, weight, rate, making, stock);
                System.out.println("Added. Unit price: Rs." + String.format("%.2f", item.calculateUnitPrice()));
            }
            case "2" -> printItems(svc.listAll());
            case "3" -> {
                System.out.print("Category: "); String cat = sc.nextLine().trim();
                printItems(svc.searchByCategory(cat));
            }
            case "4" -> {
                System.out.print("Keyword: "); String kw = sc.nextLine().trim();
                printItems(svc.searchByName(kw));
            }
            case "5" -> {
                System.out.print("Item ID: "); String id = sc.nextLine().trim();
                System.out.print("New stock quantity: "); int stock = readInt();
                svc.updateStock(id, stock);
                System.out.println("Stock updated.");
            }
            case "6" -> {
                System.out.print("Item ID: "); String id = sc.nextLine().trim();
                System.out.print("New rate per gram: "); double rate = readDouble();
                svc.updateRate(id, rate);
                System.out.println("Rate updated.");
            }
            case "7" -> {
                System.out.print("Item ID: "); String id = sc.nextLine().trim();
                svc.deleteItem(id);
                System.out.println("Deleted.");
            }
            case "8" -> {
                System.out.print("Alert threshold: "); int threshold = readInt();
                printItems(svc.lowStockItems(threshold));
            }
            default -> { /* back to main menu */ }
        }
    }

    private static void printItems(List<JewelryItem> items) {
        if (items.isEmpty()) { System.out.println("No items found."); return; }
        for (JewelryItem i : items) System.out.println(i);
    }

    // ---------------- Billing ----------------

    private static void billingMenu(BillingService billingSvc, InventoryService inventorySvc) throws JewelCraftException {
        System.out.println("\n-- Billing --");
        System.out.print("Customer ID: "); String custId = sc.nextLine().trim();

        Map<String, Integer> items = new HashMap<>();
        boolean adding = true;
        while (adding) {
            System.out.print("Item ID to add (blank to finish): "); String itemId = sc.nextLine().trim();
            if (itemId.isEmpty()) { adding = false; continue; }
            System.out.print("Quantity: "); int qty = readInt();
            items.merge(itemId, qty, Integer::sum);
        }
        System.out.print("Discount percent (0 if none): "); double discount = readDouble();

        Bill bill = billingSvc.createBill(custId, items, discount);
        printInvoice(bill);
    }

    private static void printInvoice(Bill bill) {
        System.out.println("\n========== INVOICE ==========");
        System.out.println("Bill ID   : " + bill.getBillId());
        System.out.println("Customer  : " + bill.getCustomerId());
        System.out.println("Date      : " + bill.getDateTime());
        System.out.println("------------------------------");
        bill.getItems().forEach(System.out::println);
        System.out.println("------------------------------");
        System.out.printf("Subtotal  : Rs.%.2f%n", bill.getSubtotal());
        System.out.printf("Discount  : Rs.%.2f (%.1f%%)%n", bill.getDiscountAmount(), bill.getDiscountPercent());
        System.out.printf("GST (3%%) : Rs.%.2f%n", bill.getGstAmount());
        System.out.printf("TOTAL     : Rs.%.2f%n", bill.getTotalAmount());
        System.out.println("==============================");
    }

    // ---------------- Reports ----------------

    private static void reportsMenu(ReportService svc) {
        System.out.println("\n-- Reports & Analytics --");
        System.out.println("1. Sales summary (all time)");
        System.out.println("2. Top-selling items");
        System.out.println("3. Low-stock alert");
        System.out.println("4. Back");
        System.out.print("Choose an option: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1" -> {
                ReportService.SalesSummary s = svc.salesSummary(LocalDateTime.MIN, LocalDateTime.MAX);
                System.out.println("Bills raised   : " + s.billCount);
                System.out.printf("Total revenue  : Rs.%.2f%n", s.totalRevenue);
                System.out.printf("GST collected  : Rs.%.2f%n", s.totalGstCollected);
            }
            case "2" -> {
                System.out.print("Top how many? "); int n = readInt();
                var top = svc.topSellingItems(n);
                if (top.isEmpty()) System.out.println("No sales yet.");
                top.forEach(e -> System.out.println(e.getKey() + " - " + e.getValue() + " sold"));
            }
            case "3" -> {
                System.out.print("Alert threshold: "); int threshold = readInt();
                printItems(svc.lowStockAlerts(threshold));
            }
            default -> { /* back */ }
        }
    }

    // ---------------- Customers ----------------

    private static void customerMenu(CustomerService svc) throws JewelCraftException {
        System.out.println("\n-- Customer Management --");
        System.out.println("1. Add customer");
        System.out.println("2. List customers");
        System.out.println("3. Back");
        System.out.print("Choose an option: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("Customer ID: "); String id = sc.nextLine().trim();
                System.out.print("Name: "); String name = sc.nextLine().trim();
                System.out.print("Phone (10 digits): "); String phone = sc.nextLine().trim();
                System.out.print("Email: "); String email = sc.nextLine().trim();
                svc.addCustomer(id, name, phone, email);
                System.out.println("Customer added.");
            }
            case "2" -> {
                List<Customer> customers = svc.listAll();
                if (customers.isEmpty()) System.out.println("No customers yet.");
                customers.forEach(System.out::println);
            }
            default -> { /* back */ }
        }
    }

    // ---------------- Input helpers ----------------

    private static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a whole number: ");
            }
        }
    }

    /** Pre-loads a couple of sample items/customers on first run so the app is demo-able immediately. */
    private static void seedDemoDataIfEmpty(InventoryService inventorySvc, CustomerService customerSvc) {
        if (inventorySvc.listAll().isEmpty()) {
            try {
                inventorySvc.addItem("I001", "Classic Gold Ring", "Ring", "Gold", "22K", 5.0, 6200, 500, 10);
                inventorySvc.addItem("I002", "Bridal Necklace", "Necklace", "Gold", "22K", 25.0, 6200, 800, 3);
                inventorySvc.addItem("I003", "Silver Earrings", "Earrings", "Silver", "925 Silver", 8.0, 85, 150, 20);
            } catch (JewelCraftException ignored) { }
        }
        if (customerSvc.listAll().isEmpty()) {
            try {
                customerSvc.addCustomer("C001", "Ramesh Deshmukh", "9876543210", "ramesh@example.com");
            } catch (JewelCraftException ignored) { }
        }
    }
}
