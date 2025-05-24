package domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class FinancialGoal {
    private UUID id;
    private UUID userId;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate deadline;
    private boolean isCompleted;

    public FinancialGoal(UUID userId, String name, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate deadline) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.isCompleted = false; // Default to not completed
        checkIfCompleted(); // Check completion status upon creation/update
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

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
        checkIfCompleted();
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
        checkIfCompleted();
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    // Method to update current amount (e.g., when saving towards the goal)
    public void addContribution(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
        checkIfCompleted();
    }

    // Check if the goal is completed
    private void checkIfCompleted() {
        if (this.currentAmount.compareTo(this.targetAmount) >= 0) {
            this.isCompleted = true;
        }
        // Optionally, set back to false if target increases or current decreases below target
        // else { this.isCompleted = false; }
    }

    @Override
    public String toString() {
        return "FinancialGoal{" +
                "id=" + id +
                ", userId=" + userId +
                ", name=\'" + name + "\\'" +
                ", targetAmount=" + targetAmount +
                ", currentAmount=" + currentAmount +
                ", deadline=" + deadline +
                ", isCompleted=" + isCompleted +
                '}';
    }
}

