package com.jewelcraft.repository;

import com.jewelcraft.model.JewelryItem;

import java.io.*;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Persists JewelryItem records to a plain CSV file under data/.
 * Kept deliberately dependency-free (no external DB/JDBC) so the whole
 * project runs with nothing but a JDK — no setup burden for the evaluator.
 */
public class InventoryRepository {

    private final Path filePath;

    public InventoryRepository(String dataDir) {
        this.filePath = Paths.get(dataDir, "inventory.csv");
        ensureFile();
    }

    private void ensureFile() {
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialise inventory data file: " + e.getMessage(), e);
        }
    }

    /** Loads every item into an insertion-ordered map keyed by itemId. */
    public Map<String, JewelryItem> loadAll() {
        Map<String, JewelryItem> items = new LinkedHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                JewelryItem item = JewelryItem.fromCSV(line);
                items.put(item.getItemId(), item);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read inventory data: " + e.getMessage(), e);
        }
        return items;
    }

    /** Overwrites the data file with the current in-memory state (simple, safe for small datasets). */
    public void saveAll(Map<String, JewelryItem> items) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (JewelryItem item : items.values()) {
                writer.write(item.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save inventory data: " + e.getMessage(), e);
        }
    }
}
