package com.example.financetracker.exception;

/**
 * Custom exception class for resource not found errors.
 * Used when an entity (e.g., User, Account, Transaction) cannot be found by its ID.
 */
public class NotFoundException extends Exception {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

