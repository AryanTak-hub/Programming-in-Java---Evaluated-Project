package com.jewelcraft.exception;

/** Base checked exception for every domain-specific error in the app. */
public class JewelCraftException extends Exception {
    public JewelCraftException(String message) {
        super(message);
    }
}
