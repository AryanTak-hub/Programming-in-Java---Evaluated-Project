package com.jewelcraft.service;

import com.jewelcraft.exception.InvalidInputException;
import com.jewelcraft.exception.ItemNotFoundException;
import com.jewelcraft.model.Customer;
import com.jewelcraft.repository.CustomerRepository;
import com.jewelcraft.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Supports MODULE 2 (Billing) by managing the customers a bill can be raised against. */
public class CustomerService {

    private final CustomerRepository repository;
    private final Map<String, Customer> cache;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
        this.cache = repository.loadAll();
    }

    public Customer addCustomer(String id, String name, String phone, String email)
            throws InvalidInputException {
        Validator.notBlank(id, "Customer ID");
        Validator.notBlank(name, "Customer name");
        Validator.validPhone(phone);
        if (cache.containsKey(id)) {
            throw new InvalidInputException("Customer ID '" + id + "' already exists.");
        }
        Customer c = new Customer(id, name, phone, email);
        cache.put(id, c);
        repository.saveAll(cache);
        return c;
    }

    public Customer getCustomer(String id) throws ItemNotFoundException {
        Customer c = cache.get(id);
        if (c == null) {
            throw new ItemNotFoundException("No customer found with ID '" + id + "'.");
        }
        return c;
    }

    public List<Customer> listAll() {
        return new ArrayList<>(cache.values());
    }
}
