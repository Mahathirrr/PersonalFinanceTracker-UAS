package com.example.financetracker.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Transaction {
    private UUID id;
    private UUID accountId;
    private UUID categoryId;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private String type; // "income" or "expense"

    public Transaction(UUID accountId, UUID categoryId, BigDecimal amount, LocalDate date, String description, String type) {
        this.id = UUID.randomUUID();
        this.accountId = accountId;
        this.categoryId = categoryId;
        // Ensure amount is positive for income, negative for expense if needed, or handle in service layer
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.type = type;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", categoryId=" + categoryId +
                ", amount=" + amount +
                ", date=" + date +
                ", description=\'" + description + "\'" +
                ", type=\'" + type + "\'" +
                '}';
    }
}

