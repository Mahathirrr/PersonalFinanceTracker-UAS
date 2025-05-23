package com.example.financetracker.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {
    private UUID id;
    private UUID userId; // Link to the User
    private String name;
    private BigDecimal balance;
    private String type; // e.g., Savings, Checking, Credit Card
    private boolean isActive;

    public Account(UUID userId, String name, BigDecimal balance, String type) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.name = name;
        this.balance = balance;
        this.type = type;
        this.isActive = true; // Default to active
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Method to update balance (e.g., after a transaction)
    public void updateBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", name=\'" + name + '\'\' +
                ", balance=" + balance +
                ", type=\'" + type + '\'\' +
                ", isActive=" + isActive +
                '}';
    }
}

