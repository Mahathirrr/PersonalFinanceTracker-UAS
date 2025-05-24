package service.interfaces;

import domain.Budget;
import exception.NotFoundException;
import exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Interface for managing user budgets.
 * Provides operations for creating, retrieving, updating, and deleting budgets.
 */
public interface IManageBudget {

        /**
         * Retrieves a list of all budgets for a specific user.
         *
         * @param userId The ID of the user whose budgets are to be retrieved.
         * @return A list of Budget objects.
         * @throws NotFoundException if the user is not found.
         */
        List<Budget> getBudgetList(UUID userId) throws NotFoundException;

        /**
         * Retrieves the details of a specific budget.
         *
         * @param budgetId The ID of the budget to retrieve.
         * @param userId   The ID of the user owning the budget (for authorization).
         * @return The Budget object.
         * @throws NotFoundException if the budget or user is not found.
         * @throws SecurityException if the user is not authorized to access the budget.
         */
        Budget getBudgetDetails(UUID budgetId, UUID userId) throws NotFoundException, SecurityException;

        /**
         * Creates a new budget for a user.
         *
         * @param userId      The ID of the user creating the budget.
         * @param name        The name of the new budget.
         * @param amount      The budget amount.
         * @param startDate   The start date of the budget period.
         * @param endDate     The end date of the budget period.
         * @param categoryIds A list of category IDs included in this budget.
         * @return The ID of the newly created budget.
         * @throws ValidationException if the input data is invalid (e.g., name is
         *                             empty, amount is negative, dates are invalid,
         *                             categories are invalid).
         * @throws NotFoundException   if the user or any specified category is not
         *                             found.
         * @throws SecurityException   if the user is not authorized to use the
         *                             categories.
         */
        UUID createBudget(UUID userId, String name, BigDecimal amount, LocalDate startDate, LocalDate endDate,
                        List<UUID> categoryIds)
                        throws ValidationException, NotFoundException, SecurityException;

        /**
         * Updates the details of an existing budget.
         *
         * @param budgetId    The ID of the budget to update.
         * @param userId      The ID of the user owning the budget (for authorization).
         * @param name        The new name for the budget.
         * @param amount      The new budget amount.
         * @param startDate   The new start date.
         * @param endDate     The new end date.
         * @param categoryIds The new list of category IDs.
         * @param isActive    The new active status.
         * @return true if the update was successful, false otherwise.
         * @throws ValidationException if the input data is invalid.
         * @throws NotFoundException   if the budget, user, or any specified category is
         *                             not found.
         * @throws SecurityException   if the user is not authorized to update the
         *                             budget or use the categories.
         */
        boolean updateBudget(UUID budgetId, UUID userId, String name, BigDecimal amount, LocalDate startDate,
                        LocalDate endDate, List<UUID> categoryIds, boolean isActive)
                        throws ValidationException, NotFoundException, SecurityException;

        /**
         * Deletes an existing budget.
         *
         * @param budgetId The ID of the budget to delete.
         * @param userId   The ID of the user owning the budget (for authorization).
         * @return true if the deletion was successful, false otherwise.
         * @throws NotFoundException if the budget or user is not found.
         * @throws SecurityException if the user is not authorized to delete the budget.
         */
        boolean deleteBudget(UUID budgetId, UUID userId) throws NotFoundException, SecurityException;
}
