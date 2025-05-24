package domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private UUID accountId;
    private UUID categoryId;
    private BigDecimal amount; // Should store signed amount (positive for income, negative for expense)
    private LocalDate date;
    private String description;
    private final String type; // "income" or "expense", should be final after creation

    public Transaction(UUID accountId, UUID categoryId, BigDecimal amount, LocalDate date, String description,
            String type) {
        this.id = UUID.randomUUID();
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.amount = amount; // Service layer should ensure correct sign based on type
        this.date = date;
        this.description = description;
        this.type = type;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    // Setters (for fields that might be updatable, e.g., via updateTransaction)
    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Type is generally not updatable after creation
    // public void setType(String type) {
    // this.type = type;
    // }

    @Override
    public String toString() {
        // Corrected toString with proper escaping for single quotes
        return "Transaction{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", categoryId=" + categoryId +
                ", amount=" + amount +
                ", date=" + date +
                ", description=\'" + description + "\\'" +
                ", type=\'" + type + "\\'" +
                '}';
    }
}