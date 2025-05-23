package com.example.financetracker.service.impl;

import com.example.financetracker.domain.Budget;
import com.example.financetracker.domain.Category;
import com.example.financetracker.exception.NotFoundException;
import com.example.financetracker.exception.ValidationException;
import com.example.financetracker.service.interfaces.IManageBudget;
import com.example.financetracker.service.interfaces.IManageCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of IManageBudget using in-memory storage.
 * NOTE: This is a basic implementation. A real application would use a database.
 * Assumes existence of CategoryManager and user management.
 */
public class BudgetManager implements IManageBudget {

    // In-memory storage for budgets (UserId -> BudgetId -> Budget)
    private final Map<UUID, Map<UUID, Budget>> userBudgets = new ConcurrentHashMap<>();

    // Dependencies (Inject these in a real application)
    private final IManageCategory categoryManager;
    // Simple user existence check (should be part of a dedicated user service)
    private final Map<UUID, Boolean> existingUsers = new ConcurrentHashMap<>(); // Should sync with other managers

    public BudgetManager(IManageCategory categoryManager) {
        this.categoryManager = categoryManager;
        // Ideally, user existence is managed centrally
    }

    // Helper to simulate user existence (sync with other managers or use a central service)
    public void addUser(UUID userId) {
        existingUsers.put(userId, true);
    }

    private void checkUserExists(UUID userId) throws NotFoundException {
        if (!existingUsers.containsKey(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found.");
        }
    }

    @Override
    public List<Budget> getBudgetList(UUID userId) throws NotFoundException {
        checkUserExists(userId);
        Map<UUID, Budget> budgets = userBudgets.getOrDefault(userId, new ConcurrentHashMap<>());
        return new ArrayList<>(budgets.values());
    }

    @Override
    public Budget getBudgetDetails(UUID budgetId, UUID userId) throws NotFoundException, SecurityException {
        checkUserExists(userId);
        Map<UUID, Budget> budgets = userBudgets.get(userId);
        if (budgets == null || !budgets.containsKey(budgetId)) {
            throw new NotFoundException("Budget with ID " + budgetId + " not found for user " + userId);
        }
        // Basic check: User can only access their own budgets
        Budget budget = budgets.get(budgetId);
        if (!budget.getUserId().equals(userId)) {
            // Redundant given map structure, but good practice
            throw new SecurityException("User " + userId + " is not authorized to access budget " + budgetId);
        }
        return budget;
    }

    @Override
    public UUID createBudget(UUID userId, String name, BigDecimal amount, LocalDate startDate, LocalDate endDate, List<UUID> categoryIds)
            throws ValidationException, NotFoundException, SecurityException {
        checkUserExists(userId);

        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Budget name cannot be empty.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Budget amount must be positive.");
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new ValidationException("Invalid budget period: Start date must be before or equal to end date.");
        }
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new ValidationException("Budget must include at least one category.");
        }

        // Validate categories
        for (UUID categoryId : categoryIds) {
            try {
                Category category = categoryManager.getCategoryDetails(categoryId);
                // Ensure categories are of type 'expense' for budgets (common requirement)
                if (!category.getType().equalsIgnoreCase("expense")) {
                    throw new ValidationException("Budget can only include expense categories. Category ID " + categoryId + " is of type " + category.getType());
                }
                // Add authorization checks if categories are user-specific
            } catch (NotFoundException e) {
                throw new NotFoundException("Category with ID " + categoryId + " not found.");
            }
        }

        Budget newBudget = new Budget(userId, name.trim(), amount, startDate, endDate, new ArrayList<>(categoryIds)); // Store a copy
        userBudgets.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(newBudget.getId(), newBudget);
        return newBudget.getId();
    }

    @Override
    public boolean updateBudget(UUID budgetId, UUID userId, String name, BigDecimal amount, LocalDate startDate, LocalDate endDate, List<UUID> categoryIds, boolean isActive)
            throws ValidationException, NotFoundException, SecurityException {
        Budget budget = getBudgetDetails(budgetId, userId); // Checks user, existence, auth

        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Budget name cannot be empty.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Budget amount must be positive.");
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new ValidationException("Invalid budget period: Start date must be before or equal to end date.");
        }
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new ValidationException("Budget must include at least one category.");
        }

        // Validate categories
        for (UUID categoryId : categoryIds) {
             try {
                Category category = categoryManager.getCategoryDetails(categoryId);
                 if (!category.getType().equalsIgnoreCase("expense")) {
                    throw new ValidationException("Budget can only include expense categories. Category ID " + categoryId + " is of type " + category.getType());
                }
            } catch (NotFoundException e) {
                throw new NotFoundException("Category with ID " + categoryId + " not found.");
            }
        }

        budget.setName(name.trim());
        budget.setAmount(amount);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        budget.setCategoryIds(new ArrayList<>(categoryIds)); // Store a copy
        budget.setActive(isActive);

        // In-memory update is automatic
        return true;
    }

    @Override
    public boolean deleteBudget(UUID budgetId, UUID userId) throws NotFoundException, SecurityException {
        Budget budget = getBudgetDetails(budgetId, userId); // Checks user, existence, auth

        Map<UUID, Budget> budgets = userBudgets.get(userId);
        if (budgets != null) {
            budgets.remove(budgetId);
            return true;
        }
        return false; // Should not happen
    }

    // Helper method potentially needed by ReportGenerator or other services
    public boolean isCategoryUsedInBudgets(UUID categoryId, UUID userId) {
         Map<UUID, Budget> budgets = userBudgets.get(userId);
         if (budgets == null) return false;
         return budgets.values().stream()
                 .anyMatch(budget -> budget.getCategoryIds().contains(categoryId));
    }
}

