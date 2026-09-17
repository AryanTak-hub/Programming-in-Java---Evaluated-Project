package com.jewelcraft.exception;

/** Thrown when user-supplied input fails validation (empty, negative, malformed, etc.). */
public class InvalidInputException extends JewelCraftException {
    public InvalidInputException(String message) {
        super(message);
    }
}
