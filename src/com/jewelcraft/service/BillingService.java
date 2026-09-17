package com.jewelcraft.service;

import com.jewelcraft.exception.InsufficientStockException;
import com.jewelcraft.exception.InvalidInputException;
import com.jewelcraft.exception.ItemNotFoundException;
import com.jewelcraft.model.Bill;
import com.jewelcraft.model.BillItem;
import com.jewelcraft.model.Customer;
import com.jewelcraft.model.JewelryItem;
import com.jewelcraft.repository.BillRepository;
import com.jewelcraft.util.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MODULE 2: Billing.
 * Validates stock, snapshots sale prices, deducts inventory, calculates
 * discount + 3% GST, and persists the finished invoice.
 */
public class BillingService {

    private final BillRepository billRepository;
    private final InventoryService inventoryService;
    private final CustomerService customerService;
    private int billCounter;

    public BillingService(BillRepository billRepository, InventoryService inventoryService,
                           CustomerService customerService) {
        this.billRepository = billRepository;
        this.inventoryService = inventoryService;
        this.customerService = customerService;
        this.billCounter = billRepository.loadAll().size() + 1;
    }

    /**
     * Creates and persists a bill.
     * @param requestedItems map of itemId -> quantity requested
     */
    public Bill createBill(String customerId, Map<String, Integer> requestedItems, double discountPercent)
            throws ItemNotFoundException, InsufficientStockException, InvalidInputException {

        Validator.nonNegative((int) discountPercent, "Discount percent");
        if (requestedItems == null || requestedItems.isEmpty()) {
            throw new InvalidInputException("A bill must contain at least one item.");
        }

        Customer customer = customerService.getCustomer(customerId); // throws if not found

        // Validate stock for every line BEFORE mutating anything (all-or-nothing).
        List<BillItem> lines = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : requestedItems.entrySet()) {
            String itemId = entry.getKey();
            int qty = entry.getValue();
            Validator.positive(qty, "Quantity for " + itemId);

            JewelryItem item = inventoryService.getItem(itemId); // throws ItemNotFoundException
            if (item.getStockQuantity() < qty) {
                throw new InsufficientStockException(
                        "Only " + item.getStockQuantity() + " units of '" + item.getName() + "' in stock.");
            }
            lines.add(new BillItem(itemId, item.getName(), qty, item.calculateUnitPrice()));
        }

        // All validated — now commit: deduct stock for each line.
        for (BillItem line : lines) {
            inventoryService.reduceStock(line.getItemId(), line.getQuantity());
        }

        String billId = "B" + String.format("%04d", billCounter++);
        Bill bill = new Bill(billId, customer.getCustomerId(), LocalDateTime.now(), lines, discountPercent);
        billRepository.append(bill);
        return bill;
    }

    public List<Bill> allBills() {
        return billRepository.loadAll();
    }
}
