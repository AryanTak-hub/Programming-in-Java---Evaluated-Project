package com.jewelcraft.model;

/** Represents a customer who can be billed. */
public class Customer {

    private String customerId;
    private String name;
    private String phone;
    private String email;

    public Customer(String customerId, String name, String phone, String email) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public String toCSV() {
        return String.join(",", customerId, name, phone, email);
    }

    public static Customer fromCSV(String line) {
        String[] p = line.split(",", -1);
        return new Customer(p[0], p[1], p[2], p[3]);
    }

    @Override
    public String toString() {
        return String.format("%-8s %-20s %-14s %s", customerId, name, phone, email);
    }
}
