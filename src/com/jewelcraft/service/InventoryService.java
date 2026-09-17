package com.jewelcraft.service;

import com.jewelcraft.exception.InvalidInputException;
import com.jewelcraft.exception.ItemNotFoundException;
import com.jewelcraft.model.JewelryItem;
import com.jewelcraft.repository.InventoryRepository;
import com.jewelcraft.util.Validator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MODULE 1: Inventory Management.
 * Handles add / update / delete / search / stock-alerts for jewellery items.
 * Keeps an in-memory cache (HashMap -> O(1) lookups) that mirrors the CSV file,
 * so repeated reads during a session don't touch disk (performance NFR).
 */
public class InventoryService {

    private final InventoryRepository repository;
    private final Map<String, JewelryItem> cache;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
        this.cache = repository.loadAll();
    }

    public JewelryItem addItem(String itemId, String name, String category, String metalType,
                                String purity, double weight, double rate, double makingCharge,
                                int stock) throws InvalidInputException {
        Validator.notBlank(itemId, "Item ID");
        Validator.notBlank(name, "Item name");
        Validator.positive(weight, "Weight");
        Validator.positive(rate, "Rate per gram");
        Validator.nonNegative(stock, "Stock quantity");
        if (cache.containsKey(itemId)) {
            throw new InvalidInputException("Item ID '" + itemId + "' already exists.");
        }
        JewelryItem item = new JewelryItem(itemId, name, category, metalType, purity,
                weight, rate, makingCharge, stock);
        cache.put(itemId, item);
        repository.saveAll(cache);
        return item;
    }

    public JewelryItem getItem(String itemId) throws ItemNotFoundException {
        JewelryItem item = cache.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException("No item found with ID '" + itemId + "'.");
        }
        return item;
    }

    public void updateStock(String itemId, int newStock) throws ItemNotFoundException, InvalidInputException {
        Validator.nonNegative(newStock, "Stock quantity");
        JewelryItem item = getItem(itemId);
        item.setStockQuantity(newStock);
        repository.saveAll(cache);
    }

    public void updateRate(String itemId, double newRate) throws ItemNotFoundException, InvalidInputException {
        Validator.positive(newRate, "Rate per gram");
        JewelryItem item = getItem(itemId);
        item.setRatePerGram(newRate);
        repository.saveAll(cache);
    }

    public void deleteItem(String itemId) throws ItemNotFoundException {
        if (!cache.containsKey(itemId)) {
            throw new ItemNotFoundException("No item found with ID '" + itemId + "'.");
        }
        cache.remove(itemId);
        repository.saveAll(cache);
    }

    public List<JewelryItem> listAll() {
        return new ArrayList<>(cache.values());
    }

    public List<JewelryItem> searchByCategory(String category) {
        return cache.values().stream()
                .filter(i -> i.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<JewelryItem> searchByName(String keyword) {
        String k = keyword.toLowerCase();
        return cache.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(k))
                .collect(Collectors.toList());
    }

    /** Items at or below the given stock threshold — used for reorder alerts. */
    public List<JewelryItem> lowStockItems(int threshold) {
        return cache.values().stream()
                .filter(i -> i.getStockQuantity() <= threshold)
                .collect(Collectors.toList());
    }

    /** Reduces stock after a sale. Package-private-ish: called by BillingService. */
    void reduceStock(String itemId, int quantity) {
        JewelryItem item = cache.get(itemId);
        item.setStockQuantity(item.getStockQuantity() - quantity);
        repository.saveAll(cache);
    }

    public Map<String, JewelryItem> getCacheRef() {
        return cache; // read-only usage expected by BillingService
    }
}
