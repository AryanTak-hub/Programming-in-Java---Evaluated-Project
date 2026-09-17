package com.jewelcraft.exception;

/** Thrown when a bill requests more quantity of an item than is in stock. */
public class InsufficientStockException extends JewelCraftException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
