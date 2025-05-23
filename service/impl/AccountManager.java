package com.example.financetracker.service.impl;

import com.example.financetracker.domain.Account;
import com.example.financetracker.exception.NotFoundException;
import com.example.financetracker.exception.ValidationException;
import com.example.financetracker.service.interfaces.IManageAccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of IManageAccount using in-memory storage.
 * NOTE: This is a basic implementation for demonstration. A real application
 * would use a persistent data store (e.g., database).
 */
public class AccountManager implements IManageAccount {

    // In-memory storage for accounts (UserId -> AccountId -> Account)
    // Using ConcurrentHashMap for potential thread safety, though full concurrency control is not implemented here.
    private final Map<UUID, Map<UUID, Account>> userAccounts = new ConcurrentHashMap<>();
    // Simple user existence check (replace with actual user management)
    private final Map<UUID, Boolean> existingUsers = new ConcurrentHashMap<>();

    // Helper to simulate user existence
    public void addUser(UUID userId) {
        existingUsers.put(userId, true);
    }

    private void checkUserExists(UUID userId) throws NotFoundException {
        if (!existingUsers.containsKey(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found.");
        }
    }

    @Override
    public List<Account> getAccountList(UUID userId) throws NotFoundException {
        checkUserExists(userId);
        Map<UUID, Account> accounts = userAccounts.getOrDefault(userId, new ConcurrentHashMap<>());
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccountDetails(UUID accountId, UUID userId) throws NotFoundException, SecurityException {
        checkUserExists(userId);
        Map<UUID, Account> accounts = userAccounts.get(userId);
        if (accounts == null || !accounts.containsKey(accountId)) {
            throw new NotFoundException("Account with ID " + accountId + " not found for user " + userId);
        }
        // Basic check: User can only access their own accounts
        Account account = accounts.get(accountId);
        if (!account.getUserId().equals(userId)) {
             // This check is somewhat redundant given the map structure, but good practice
            throw new SecurityException("User " + userId + " is not authorized to access account " + accountId);
        }
        return account;
    }

    @Override
    public UUID createAccount(UUID userId, String name, BigDecimal balance, String type) throws ValidationException, NotFoundException {
        checkUserExists(userId);
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Account name cannot be empty.");
        }
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            // Allowing zero balance, but not negative for initial creation
            throw new ValidationException("Initial balance cannot be negative.");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new ValidationException("Account type cannot be empty.");
        }

        Account newAccount = new Account(userId, name, balance, type);
        userAccounts.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(newAccount.getId(), newAccount);
        return newAccount.getId();
    }

    @Override
    public boolean updateAccount(UUID accountId, UUID userId, String name, String type, boolean isActive) throws ValidationException, NotFoundException, SecurityException {
        Account account = getAccountDetails(accountId, userId); // This also performs user and auth checks

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Account name cannot be empty.");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new ValidationException("Account type cannot be empty.");
        }

        account.setName(name);
        account.setType(type);
        account.setActive(isActive);

        // In a real DB scenario, you'd save the updated account here.
        // For in-memory, the object reference is updated.
        return true;
    }

    @Override
    public boolean deleteAccount(UUID accountId, UUID userId) throws NotFoundException, SecurityException, ValidationException {
        Account account = getAccountDetails(accountId, userId); // Performs user and auth checks

        // Basic check: Prevent deletion if account has non-zero balance (example rule)
        // A real app would check for associated transactions.
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new ValidationException("Cannot delete account with non-zero balance. Balance: " + account.getBalance());
        }

        Map<UUID, Account> accounts = userAccounts.get(userId);
        if (accounts != null) {
            accounts.remove(accountId);
            return true;
        }
        return false; // Should not happen if getAccountDetails succeeded
    }
}

