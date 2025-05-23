package com.example.financetracker.service.interfaces;

import com.example.financetracker.domain.FinancialGoal;
import com.example.financetracker.exception.NotFoundException;
import com.example.financetracker.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Interface for managing user financial goals.
 * Provides operations for creating, retrieving, updating, deleting, and tracking goals.
 */
public interface IManageFinancialGoal {

    /**
     * Retrieves a list of all financial goals for a specific user.
     *
     * @param userId The ID of the user whose goals are to be retrieved.
     * @return A list of FinancialGoal objects.
     * @throws NotFoundException if the user is not found.
     */
    List<FinancialGoal> getFinancialGoalList(UUID userId) throws NotFoundException;

    /**
     * Retrieves the details of a specific financial goal.
     *
     * @param goalId The ID of the goal to retrieve.
     * @param userId The ID of the user owning the goal (for authorization).
     * @return The FinancialGoal object.
     * @throws NotFoundException if the goal or user is not found.
     * @throws SecurityException if the user is not authorized to access the goal.
     */
    FinancialGoal getFinancialGoalDetails(UUID goalId, UUID userId) throws NotFoundException, SecurityException;

    /**
     * Creates a new financial goal for a user.
     *
     * @param userId The ID of the user creating the goal.
     * @param name The name of the new goal.
     * @param targetAmount The target amount for the goal.
     * @param currentAmount The initial amount saved towards the goal.
     * @param deadline The deadline to achieve the goal.
     * @return The ID of the newly created financial goal.
     * @throws ValidationException if the input data is invalid (e.g., name is empty, target amount is non-positive, current amount exceeds target, deadline is in the past).
     * @throws NotFoundException if the user is not found.
     */
    UUID createFinancialGoal(UUID userId, String name, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate deadline)
            throws ValidationException, NotFoundException;

    /**
     * Updates the details of an existing financial goal.
     *
     * @param goalId The ID of the goal to update.
     * @param userId The ID of the user owning the goal (for authorization).
     * @param name The new name for the goal.
     * @param targetAmount The new target amount.
     * @param currentAmount The new current amount saved.
     * @param deadline The new deadline.
     * @return true if the update was successful, false otherwise.
     * @throws ValidationException if the input data is invalid.
     * @throws NotFoundException if the goal or user is not found.
     * @throws SecurityException if the user is not authorized to update the goal.
     */
    boolean updateFinancialGoal(UUID goalId, UUID userId, String name, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate deadline)
            throws ValidationException, NotFoundException, SecurityException;

    /**
     * Adds a contribution towards a specific financial goal.
     *
     * @param goalId The ID of the goal to contribute to.
     * @param userId The ID of the user making the contribution (for authorization).
     * @param amount The amount to contribute (must be positive).
     * @return true if the contribution was added successfully, false otherwise.
     * @throws ValidationException if the amount is non-positive.
     * @throws NotFoundException if the goal or user is not found.
     * @throws SecurityException if the user is not authorized to contribute to the goal.
     */
    boolean addContribution(UUID goalId, UUID userId, BigDecimal amount) throws ValidationException, NotFoundException, SecurityException;

    /**
     * Deletes an existing financial goal.
     *
     * @param goalId The ID of the goal to delete.
     * @param userId The ID of the user owning the goal (for authorization).
     * @return true if the deletion was successful, false otherwise.
     * @throws NotFoundException if the goal or user is not found.
     * @throws SecurityException if the user is not authorized to delete the goal.
     */
    boolean deleteFinancialGoal(UUID goalId, UUID userId) throws NotFoundException, SecurityException;
}

