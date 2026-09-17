package com.jewelcraft.util;

import com.jewelcraft.exception.InvalidInputException;

/**
 * Centralised input validation. Every service method routes user input through
 * here before it touches business logic or storage — this is the app's main
 * defence against malformed or malicious data (the "Security" non-functional
 * requirement).
 */
public final class Validator {

    private Validator() { } // utility class, no instances

    public static void notBlank(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
    }

    public static void positive(double value, String fieldName) throws InvalidInputException {
        if (value <= 0) {
            throw new InvalidInputException(fieldName + " must be a positive number.");
        }
    }

    public static void nonNegative(int value, String fieldName) throws InvalidInputException {
        if (value < 0) {
            throw new InvalidInputException(fieldName + " cannot be negative.");
        }
    }

    public static void validPhone(String phone) throws InvalidInputException {
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new InvalidInputException("Phone number must be exactly 10 digits.");
        }
    }

    public static double parsePositiveDouble(String raw, String fieldName) throws InvalidInputException {
        try {
            double v = Double.parseDouble(raw.trim());
            positive(v, fieldName);
            return v;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid number.");
        }
    }

    public static int parseNonNegativeInt(String raw, String fieldName) throws InvalidInputException {
        try {
            int v = Integer.parseInt(raw.trim());
            nonNegative(v, fieldName);
            return v;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a whole number.");
        }
    }
}
