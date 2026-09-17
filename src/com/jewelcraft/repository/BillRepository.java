package com.jewelcraft.repository;

import com.jewelcraft.model.Bill;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Bills are append-only (an invoice, once raised, is never silently edited) —
 * so this repository appends new bills rather than rewriting the whole file.
 */
public class BillRepository {

    private final Path filePath;

    public BillRepository(String dataDir) {
        this.filePath = Paths.get(dataDir, "bills.csv");
        ensureFile();
    }

    private void ensureFile() {
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialise bill data file: " + e.getMessage(), e);
        }
    }

    public void append(Bill bill) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath, StandardOpenOption.APPEND)) {
            writer.write(bill.toCSV());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bill: " + e.getMessage(), e);
        }
    }

    public List<Bill> loadAll() {
        List<Bill> bills = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                bills.add(Bill.fromCSV(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read bill data: " + e.getMessage(), e);
        }
        return bills;
    }
}
