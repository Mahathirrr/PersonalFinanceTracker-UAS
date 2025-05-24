package service.impl;

import domain.FinancialGoal;
import exception.NotFoundException;
import exception.ValidationException;
import service.interfaces.IManageFinancialGoal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of IManageFinancialGoal using in-memory storage.
 * NOTE: This is a basic implementation. A real application would use a
 * database.
 * Assumes user existence is managed elsewhere or synced.
 */
public class FinancialGoalManager implements IManageFinancialGoal {

    // In-memory storage for financial goals (UserId -> GoalId -> FinancialGoal)
    private final Map<UUID, Map<UUID, FinancialGoal>> userFinancialGoals = new ConcurrentHashMap<>();

    // Simple user existence check (should be part of a dedicated user service)
    private final Map<UUID, Boolean> existingUsers = new ConcurrentHashMap<>(); // Should sync with other managers

    public FinancialGoalManager() {
        // Ideally, user existence is managed centrally
    }

    // Helper to simulate user existence (sync with other managers or use a central
    // service)
    public void addUser(UUID userId) {
        existingUsers.put(userId, true);
    }

    private void checkUserExists(UUID userId) throws NotFoundException {
        if (!existingUsers.containsKey(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found.");
        }
    }

    @Override
    public List<FinancialGoal> getFinancialGoalList(UUID userId) throws NotFoundException {
        checkUserExists(userId);
        Map<UUID, FinancialGoal> goals = userFinancialGoals.getOrDefault(userId, new ConcurrentHashMap<>());
        return new ArrayList<>(goals.values());
    }

    @Override
    public FinancialGoal getFinancialGoalDetails(UUID goalId, UUID userId) throws NotFoundException, SecurityException {
        checkUserExists(userId);
        Map<UUID, FinancialGoal> goals = userFinancialGoals.get(userId);
        if (goals == null || !goals.containsKey(goalId)) {
            throw new NotFoundException("Financial Goal with ID " + goalId + " not found for user " + userId);
        }
        // Basic check: User can only access their own goals
        FinancialGoal goal = goals.get(goalId);
        if (!goal.getUserId().equals(userId)) {
            // Redundant given map structure, but good practice
            throw new SecurityException("User " + userId + " is not authorized to access financial goal " + goalId);
        }
        return goal;
    }

    @Override
    public UUID createFinancialGoal(UUID userId, String name, BigDecimal targetAmount, BigDecimal currentAmount,
            LocalDate deadline)
            throws ValidationException, NotFoundException {
        checkUserExists(userId);

        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Financial goal name cannot be empty.");
        }
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Target amount must be positive.");
        }
        if (currentAmount == null || currentAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Current amount cannot be negative.");
        }
        if (currentAmount.compareTo(targetAmount) > 0) {
            throw new ValidationException("Current amount cannot exceed target amount.");
        }
        if (deadline == null || deadline.isBefore(LocalDate.now())) {
            throw new ValidationException("Deadline must be in the future.");
        }

        FinancialGoal newGoal = new FinancialGoal(userId, name.trim(), targetAmount, currentAmount, deadline);
        userFinancialGoals.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(newGoal.getId(), newGoal);
        return newGoal.getId();
    }

    @Override
    public boolean updateFinancialGoal(UUID goalId, UUID userId, String name, BigDecimal targetAmount,
            BigDecimal currentAmount, LocalDate deadline)
            throws ValidationException, NotFoundException, SecurityException {
        FinancialGoal goal = getFinancialGoalDetails(goalId, userId); // Checks user, existence, auth

        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Financial goal name cannot be empty.");
        }
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Target amount must be positive.");
        }
        if (currentAmount == null || currentAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Current amount cannot be negative.");
        }
        // Allow current amount to exceed target during update? Or cap it?
        // Capping it for this example:
        if (currentAmount.compareTo(targetAmount) > 0) {
            // throw new ValidationException("Current amount cannot exceed target amount.");
            currentAmount = targetAmount; // Cap at target amount
        }
        if (deadline == null || deadline.isBefore(LocalDate.now())) {
            // Allow updating deadline to past? Maybe for marking historical goals?
            // Keeping validation for future deadline for active goals.
            if (!goal.isCompleted()) { // Only enforce future deadline for non-completed goals
                throw new ValidationException("Deadline must be in the future for active goals.");
            }
        }

        goal.setName(name.trim());
        goal.setTargetAmount(targetAmount);
        goal.setCurrentAmount(currentAmount); // This will also trigger re-check of completion status in the domain
                                              // object
        goal.setDeadline(deadline);

        // In-memory update is automatic
        return true;
    }

    @Override
    public boolean addContribution(UUID goalId, UUID userId, BigDecimal amount)
            throws ValidationException, NotFoundException, SecurityException {
        FinancialGoal goal = getFinancialGoalDetails(goalId, userId); // Checks user, existence, auth

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Contribution amount must be positive.");
        }
        if (goal.isCompleted()) {
            throw new ValidationException("Cannot add contribution to an already completed goal.");
        }

        goal.addContribution(amount);

        // In-memory update is automatic
        return true;
    }

    @Override
    public boolean deleteFinancialGoal(UUID goalId, UUID userId) throws NotFoundException, SecurityException {
        FinancialGoal goal = getFinancialGoalDetails(goalId, userId); // Checks user, existence, auth

        Map<UUID, FinancialGoal> goals = userFinancialGoals.get(userId);
        if (goals != null) {
            goals.remove(goalId);
            return true;
        }
        return false; // Should not happen
    }
}
