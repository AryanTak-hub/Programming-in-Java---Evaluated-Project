package com.jewelcraft.exception;

/** Thrown when an item/customer/bill ID does not exist in the store. */
public class ItemNotFoundException extends JewelCraftException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
