package domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a financial account.
 */
public class Account {
    private final UUID id;
    private final UUID userId; // Link to the user who owns the account
    private String name;
    private BigDecimal balance;
    private String type; // e.g., Checking, Savings, Credit Card
    private boolean isActive;

    public Account(UUID userId, String name, BigDecimal balance, String type) {
        this.id = UUID.randomUUID(); // Generate unique ID for the account
        this.userId = userId;
        this.name = name;
        this.balance = balance;
        this.type = type;
        this.isActive = true; // Default to active
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters (for updatable fields)
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Method to update balance (ensure thread safety if needed in concurrent environment)
    public void updateBalance(BigDecimal amount) {
        // Consider validation or rules here (e.g., prevent overdraft for certain types)
        this.balance = this.balance.add(amount);
    }

    @Override
    public String toString() {
        // Corrected toString with proper escaping for single quotes
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", type='" + type + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}