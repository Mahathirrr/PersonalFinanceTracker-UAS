package service.impl;

import domain.Account;
import exception.NotFoundException;
import exception.ValidationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of AccountManager using in-memory storage.
 * NOTE: Renamed from AccountManagerImpl based on user feedback.
 */
// Assuming AccountManager interface exists in service package
// If not, this class should just be AccountManager without implements
public class AccountManager /* implements service.AccountManager */ {

    private final Map<UUID, Map<UUID, Account>> userAccounts = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> existingUsers = new ConcurrentHashMap<>();

    // Getter for existingUsers map (needed by other managers)
    public Map<UUID, Boolean> getExistingUsers() {
        return new ConcurrentHashMap<>(existingUsers); // Return a copy for safety
    }

    // Helper to simulate user existence
    public void addUser(UUID userId) {
        existingUsers.put(userId, true);
    }

    // Helper to check user existence (can be used by other managers)
    public boolean userExists(UUID userId) {
        return existingUsers.containsKey(userId);
    }

    private void checkUserExists(UUID userId) throws NotFoundException {
        if (!userExists(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found.");
        }
    }

    // Method signature might need to match an interface if one exists
    // @Override
    public List<Account> getAccountList(UUID userId) throws NotFoundException {
        checkUserExists(userId);
        Map<UUID, Account> accounts = userAccounts.getOrDefault(userId, new ConcurrentHashMap<>());
        return new ArrayList<>(accounts.values());
    }

    // Method signature might need to match an interface if one exists
    // @Override
    public Account getAccount(UUID accountId) throws NotFoundException {
        // Find account across all users (might need refinement based on requirements)
        for (Map<UUID, Account> userMap : userAccounts.values()) {
            if (userMap.containsKey(accountId)) {
                return userMap.get(accountId);
            }
        }
        throw new NotFoundException("Account with ID " + accountId + " not found.");
    }

    // Overloaded method to get account details ensuring user ownership
    public Account getAccount(UUID accountId, UUID userId) throws NotFoundException, SecurityException {
        checkUserExists(userId);
        Map<UUID, Account> accounts = userAccounts.get(userId);
        if (accounts == null || !accounts.containsKey(accountId)) {
            throw new NotFoundException("Account with ID " + accountId + " not found for user " + userId);
        }
        Account account = accounts.get(accountId);
        // Redundant check given structure, but good practice
        if (!account.getUserId().equals(userId)) {
            throw new SecurityException("User " + userId + " is not authorized to access account " + accountId);
        }
        return account;
    }

    // Method signature might need to match an interface if one exists
    // @Override
    public Account createAccount(UUID userId, String name, BigDecimal balance, String type)
            throws ValidationException, NotFoundException {
        checkUserExists(userId);
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Account name cannot be empty.");
        }
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Initial balance cannot be negative.");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new ValidationException("Account type cannot be empty.");
        }

        Account newAccount = new Account(userId, name, balance, type);
        userAccounts.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(newAccount.getId(), newAccount);
        addUser(userId); // Ensure user is marked as existing
        return newAccount; // Return the created account object
    }

    // Method signature might need to match an interface if one exists
    // @Override
    public boolean updateAccount(UUID accountId, UUID userId, String name, String type, boolean isActive)
            throws ValidationException, NotFoundException, SecurityException {
        Account account = getAccount(accountId, userId); // Use the user-specific getter

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Account name cannot be empty.");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new ValidationException("Account type cannot be empty.");
        }

        account.setName(name);
        account.setType(type);
        account.setActive(isActive);
        return true;
    }

    // Method signature might need to match an interface if one exists
    // @Override
    public boolean deleteAccount(UUID accountId, UUID userId)
            throws NotFoundException, SecurityException, ValidationException {
        Account account = getAccount(accountId, userId); // Use the user-specific getter

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new ValidationException(
                    "Cannot delete account with non-zero balance. Balance: " + account.getBalance());
        }

        Map<UUID, Account> accounts = userAccounts.get(userId);
        if (accounts != null) {
            accounts.remove(accountId);
            // Optional: Remove user if they have no more accounts
            // if (accounts.isEmpty()) { userAccounts.remove(userId);
            // existingUsers.remove(userId); }
            return true;
        }
        return false;
    }
}