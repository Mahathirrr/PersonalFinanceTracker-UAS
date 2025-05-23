package com.example.financetracker.exception;

/**
 * Custom exception class for validation errors.
 * Used when input data fails validation checks.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

