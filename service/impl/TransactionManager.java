package service.impl;

// Import domain classes
import domain.Account;
import domain.Category;
import domain.Transaction;
// Import exception classes
import exception.NotFoundException;
import exception.ValidationException;
// Import implementation classes directly
import service.impl.AccountManager;
import service.impl.CategoryManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of TransactionManager using in-memory storage.
 */
public class TransactionManager {

    private final Map<UUID, Map<UUID, Transaction>> userTransactions = new ConcurrentHashMap<>();

    // Dependencies (Use concrete implementation classes)
    private final AccountManager accountManager;
    private final CategoryManager categoryManager;

    public TransactionManager(AccountManager accountManager, CategoryManager categoryManager) {
        this.accountManager = accountManager;
        this.categoryManager = categoryManager;
    }

    // Rely on AccountManager for user existence check
    private void checkUserExists(UUID userId) throws NotFoundException {
        if (!accountManager.userExists(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found.");
        }
    }

    public List<Transaction> getTransactionsByUser(UUID userId, LocalDate startDate, LocalDate endDate)
            throws NotFoundException {
        checkUserExists(userId);
        Map<UUID, Transaction> transactions = userTransactions.getOrDefault(userId, new ConcurrentHashMap<>());

        Stream<Transaction> stream = transactions.values().stream();

        if (startDate != null) {
            stream = stream.filter(t -> !t.getDate().isBefore(startDate));
        }
        if (endDate != null) {
            stream = stream.filter(t -> !t.getDate().isAfter(endDate));
        }

        return stream.collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByAccount(UUID accountId) throws NotFoundException {
        Account account = accountManager.getAccount(accountId);
        UUID userId = account.getUserId();
        checkUserExists(userId);

        Map<UUID, Transaction> transactions = userTransactions.getOrDefault(userId, new ConcurrentHashMap<>());
        return transactions.values().stream()
                .filter(t -> t.getAccountId().equals(accountId))
                .collect(Collectors.toList());
    }

    public Transaction getTransaction(UUID transactionId, UUID userId) throws NotFoundException, SecurityException {
        checkUserExists(userId);
        Map<UUID, Transaction> transactions = userTransactions.get(userId);
        if (transactions == null || !transactions.containsKey(transactionId)) {
            throw new NotFoundException("Transaction with ID " + transactionId + " not found for user " + userId);
        }
        // Assuming the map structure inherently enforces security (user can only access
        // their map)
        return transactions.get(transactionId);
    }

    public Transaction recordTransaction(UUID userId, UUID accountId, String categoryName, String type,
            BigDecimal amount, LocalDate date)
            throws ValidationException, NotFoundException, SecurityException {
        checkUserExists(userId);

        if (accountId == null || categoryName == null || categoryName.trim().isEmpty() || type == null
                || type.trim().isEmpty() || amount == null || date == null) {
            throw new ValidationException("Account ID, category name, type, amount, and date are required.");
        }
        if (!(type.equalsIgnoreCase("income") || type.equalsIgnoreCase("expense"))) {
            throw new ValidationException("Invalid transaction type: " + type + ". Must be 'income' or 'expense'.");
        }
        BigDecimal absAmount = amount.abs();
        if (absAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new ValidationException("Transaction amount cannot be zero.");
        }

        Account account = accountManager.getAccount(accountId, userId); // Checks user auth
        Category category = categoryManager.getCategoryByName(categoryName); // Assuming method exists

        if (!category.getType().equalsIgnoreCase(type)) {
            throw new ValidationException(
                    "Transaction type '" + type + "' does not match category type '" + category.getType() + "'.");
        }

        BigDecimal signedAmount = type.equalsIgnoreCase("income") ? absAmount : absAmount.negate();

        Transaction newTransaction = new Transaction(accountId, category.getId(), signedAmount, date, categoryName,
                type);
        userTransactions.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(newTransaction.getId(),
                newTransaction);

        account.updateBalance(signedAmount);
        // TODO: Persist account change if using a DB

        return newTransaction;
    }

    public boolean updateTransaction(UUID transactionId, UUID userId, UUID accountId, UUID categoryId,
            BigDecimal amount, LocalDate date, String description)
            throws ValidationException, NotFoundException, SecurityException {
        checkUserExists(userId);
        Transaction existingTransaction = getTransaction(transactionId, userId);

        if (accountId == null || categoryId == null || amount == null || date == null || description == null) {
            throw new ValidationException("All transaction fields are required for update.");
        }
        BigDecimal absAmount = amount.abs();
        if (absAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new ValidationException("Transaction amount cannot be zero.");
        }

        Account newAccount = accountManager.getAccount(accountId, userId);
        Category newCategory = categoryManager.getCategory(categoryId); // Assuming getCategory(UUID) exists

        BigDecimal newSignedAmount = existingTransaction.getType().equalsIgnoreCase("income") ? absAmount
                : absAmount.negate();

        if (!newCategory.getType().equalsIgnoreCase(existingTransaction.getType())) {
            throw new ValidationException("Update Error: New category type '" + newCategory.getType()
                    + "' does not match existing transaction type '" + existingTransaction.getType()
                    + "'. Type change not supported here.");
        }

        Account oldAccount = accountManager.getAccount(existingTransaction.getAccountId(), userId);
        oldAccount.updateBalance(existingTransaction.getAmount().negate());
        newAccount.updateBalance(newSignedAmount);
        // TODO: Persist account changes

        existingTransaction.setAccountId(accountId);
        existingTransaction.setCategoryId(categoryId);
        existingTransaction.setAmount(newSignedAmount);
        existingTransaction.setDate(date);
        existingTransaction.setDescription(description);

        return true;
    }

    public boolean deleteTransaction(UUID transactionId, UUID userId) throws NotFoundException, SecurityException {
        checkUserExists(userId);
        Transaction transactionToDelete = getTransaction(transactionId, userId);

        Account account = accountManager.getAccount(transactionToDelete.getAccountId(), userId);
        account.updateBalance(transactionToDelete.getAmount().negate());
        // TODO: Persist account change

        Map<UUID, Transaction> transactions = userTransactions.get(userId);
        if (transactions != null) {
            transactions.remove(transactionId);
            return true;
        }
        return false;
    }
}