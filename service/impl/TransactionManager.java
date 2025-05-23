package com.example.financetracker.service.impl;

import com.example.financetracker.domain.Account;
import com.example.financetracker.domain.Category;
import com.example.financetracker.domain.Transaction;
import com.example.financetracker.exception.NotFoundException;
import com.example.financetracker.exception.ValidationException;
import com.example.financetracker.service.interfaces.IManageAccount;
import com.example.financetracker.service.interfaces.IManageCategory;
import com.example.financetracker.service.interfaces.IManageTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of IManageTransaction using in-memory storage.
 * NOTE: This is a basic implementation. A real application would use a database
 * and handle concurrency and atomicity more robustly (e.g., using transactions).
 * Assumes existence of AccountManager and CategoryManager instances.
 */
public class TransactionManager implements IManageTransaction {

    // In-memory storage for transactions (UserId -> TransactionId -> Transaction)
    private final Map<UUID, Map<UUID, Transaction>> userTransactions = new ConcurrentHashMap<>();

    // Dependencies (Inject these in a real application using a framework like Spring)
    private final IManageAccount accountManager;
    private final IManageCategory categoryManager;
    // Simple user existence check (should be part of a dedicated user service)
    private final Map<UUID, Boolean> existingUsers = new ConcurrentHashMap<>();

    public TransactionManager(IManageAccount accountManager, IManageCategory categoryManager) {
        this.accountManager = accountManager;
        this.categoryManager = categoryManager;
        // Simulate user existence from AccountManager if possible, or manage separately
        if (accountManager instanceof AccountManager) {
            this.existingUsers.putAll(((AccountManager) accountManager).existingUsers);
        }
    }

    // Helper to simulate user existence
    public void addUser(UUID userId) {
        existingUsers.put(userId, true);
        // Also add to AccountManager if needed
        if (accountManager instanceof AccountManager) {
            ((AccountManager) accountManager).addUser(userId);
        }
    }

    private void checkUserExists(UUID userId) throws NotFoundException {
        if (!existingUsers.containsKey(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found.");
        }
    }

    @Override
    public List<Transaction> getTransactionList(UUID userId, Map<String, Object> filters) throws NotFoundException {
        checkUserExists(userId);
        Map<UUID, Transaction> transactions = userTransactions.getOrDefault(userId, new ConcurrentHashMap<>());

        Stream<Transaction> stream = transactions.values().stream();

        // Apply filters (example filters)
        if (filters != null) {
            if (filters.containsKey("accountId")) {
                UUID accountId = (UUID) filters.get("accountId");
                stream = stream.filter(t -> t.getAccountId().equals(accountId));
            }
            if (filters.containsKey("categoryId")) {
                UUID categoryId = (UUID) filters.get("categoryId");
                stream = stream.filter(t -> t.getCategoryId().equals(categoryId));
            }
            if (filters.containsKey("startDate")) {
                LocalDate startDate = (LocalDate) filters.get("startDate");
                stream = stream.filter(t -> !t.getDate().isBefore(startDate));
            }
            if (filters.containsKey("endDate")) {
                LocalDate endDate = (LocalDate) filters.get("endDate");
                stream = stream.filter(t -> !t.getDate().isAfter(endDate));
            }
            if (filters.containsKey("type")) {
                String type = (String) filters.get("type");
                stream = stream.filter(t -> t.getType().equalsIgnoreCase(type));
            }
        }

        return stream.collect(Collectors.toList());
    }

    @Override
    public Transaction getTransactionDetails(UUID transactionId, UUID userId) throws NotFoundException, SecurityException {
        checkUserExists(userId);
        Map<UUID, Transaction> transactions = userTransactions.get(userId);
        if (transactions == null || !transactions.containsKey(transactionId)) {
            throw new NotFoundException("Transaction with ID " + transactionId + " not found for user " + userId);
        }
        // Basic check: User can only access their own transactions
        // This is implicitly handled by the map structure, but an explicit check might be needed
        // depending on how transaction data is stored/retrieved in a real system.
        return transactions.get(transactionId);
    }

    @Override
    public UUID createTransaction(UUID userId, UUID accountId, UUID categoryId, BigDecimal amount, LocalDate date, String description, String type)
            throws ValidationException, NotFoundException, SecurityException {
        checkUserExists(userId);

        // Validate inputs
        if (accountId == null || categoryId == null || amount == null || date == null || type == null || description == null) {
            throw new ValidationException("All transaction fields are required.");
        }
        if (!(type.equalsIgnoreCase("income") || type.equalsIgnoreCase("expense"))) {
            throw new ValidationException("Invalid transaction type: " + type);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Transaction amount must be positive.");
        }

        // Check existence and authorization for account and category
        Account account = accountManager.getAccountDetails(accountId, userId); // Checks user, existence, auth
        Category category = categoryManager.getCategoryDetails(categoryId); // Assuming categories are global or user check happens here

        // Validate category type matches transaction type
        if (!category.getType().equalsIgnoreCase(type)) {
            throw new ValidationException("Transaction type '" + type + "' does not match category type '" + category.getType() + "'.");
        }

        // Create transaction
        Transaction newTransaction = new Transaction(accountId, categoryId, amount, date, description, type);
        userTransactions.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(newTransaction.getId(), newTransaction);

        // Update account balance (CRITICAL: Needs atomicity in a real system)
        BigDecimal balanceChange = type.equalsIgnoreCase("income") ? amount : amount.negate();
        account.updateBalance(balanceChange);
        // Persist account change (in a real DB)
        // accountManager.updateAccount(...); // Need a way to update just the balance or save the account object

        return newTransaction.getId();
    }

    @Override
    public boolean updateTransaction(UUID transactionId, UUID userId, UUID accountId, UUID categoryId, BigDecimal amount, LocalDate date, String description)
            throws ValidationException, NotFoundException, SecurityException {
        checkUserExists(userId);
        Transaction existingTransaction = getTransactionDetails(transactionId, userId); // Checks user, existence, auth

        // Validate inputs
        if (accountId == null || categoryId == null || amount == null || date == null || description == null) {
            throw new ValidationException("All transaction fields are required for update.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Transaction amount must be positive.");
        }

        // Check existence and authorization for new account and category
        Account newAccount = accountManager.getAccountDetails(accountId, userId);
        Category newCategory = categoryManager.getCategoryDetails(categoryId);

        // Validate category type matches transaction type (assuming type doesn't change)
        if (!newCategory.getType().equalsIgnoreCase(existingTransaction.getType())) {
            throw new ValidationException("Update Error: New category type '" + newCategory.getType() + "' does not match existing transaction type '" + existingTransaction.getType() + "'.");
        }

        // --- Update Account Balances (CRITICAL: Needs atomicity) ---
        // 1. Revert old transaction effect
        Account oldAccount = accountManager.getAccountDetails(existingTransaction.getAccountId(), userId);
        BigDecimal oldBalanceChange = existingTransaction.getType().equalsIgnoreCase("income") ? existingTransaction.getAmount() : existingTransaction.getAmount().negate();
        oldAccount.updateBalance(oldBalanceChange.negate()); // Revert
        // Persist old account change

        // 2. Apply new transaction effect
        BigDecimal newBalanceChange = existingTransaction.getType().equalsIgnoreCase("income") ? amount : amount.negate();
        newAccount.updateBalance(newBalanceChange); // Apply new
        // Persist new account change
        // --- End Balance Update --- 

        // Update transaction details
        existingTransaction.setAccountId(accountId);
        existingTransaction.setCategoryId(categoryId);
        existingTransaction.setAmount(amount);
        existingTransaction.setDate(date);
        existingTransaction.setDescription(description);
        // Type is assumed not to change in this method signature

        // Persist transaction change (in-memory is automatic)
        return true;
    }

    @Override
    public boolean deleteTransaction(UUID transactionId, UUID userId) throws NotFoundException, SecurityException {
        checkUserExists(userId);
        Transaction transactionToDelete = getTransactionDetails(transactionId, userId); // Checks user, existence, auth

        // --- Update Account Balance (CRITICAL: Needs atomicity) ---
        // Revert transaction effect
        Account account = accountManager.getAccountDetails(transactionToDelete.getAccountId(), userId);
        BigDecimal balanceChange = transactionToDelete.getType().equalsIgnoreCase("income") ? transactionToDelete.getAmount() : transactionToDelete.getAmount().negate();
        account.updateBalance(balanceChange.negate()); // Revert
        // Persist account change
        // --- End Balance Update --- 

        // Remove transaction from storage
        Map<UUID, Transaction> transactions = userTransactions.get(userId);
        if (transactions != null) {
            transactions.remove(transactionId);
            return true;
        }
        return false; // Should not happen
    }
}

