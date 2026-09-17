package com.jewelcraft.repository;

import com.jewelcraft.model.Customer;

import java.io.*;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Persists Customer records to data/customers.csv. */
public class CustomerRepository {

    private final Path filePath;

    public CustomerRepository(String dataDir) {
        this.filePath = Paths.get(dataDir, "customers.csv");
        ensureFile();
    }

    private void ensureFile() {
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialise customer data file: " + e.getMessage(), e);
        }
    }

    public Map<String, Customer> loadAll() {
        Map<String, Customer> customers = new LinkedHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                Customer c = Customer.fromCSV(line);
                customers.put(c.getCustomerId(), c);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read customer data: " + e.getMessage(), e);
        }
        return customers;
    }

    public void saveAll(Map<String, Customer> customers) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Customer c : customers.values()) {
                writer.write(c.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save customer data: " + e.getMessage(), e);
        }
    }
}
