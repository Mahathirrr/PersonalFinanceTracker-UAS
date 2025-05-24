package service.interfaces;

import domain.Transaction;
import exception.NotFoundException;
import exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for managing financial transactions.
 * Provides operations for creating, retrieving, updating, and deleting
 * transactions.
 */
public interface IManageTransaction {

        /**
         * Retrieves a list of transactions based on specified filters.
         *
         * @param userId  The ID of the user whose transactions are to be retrieved.
         * @param filters A map containing filter criteria (e.g., accountId, categoryId,
         *                date range, type).
         * @return A list of Transaction objects matching the filters.
         * @throws NotFoundException if the user is not found.
         */
        List<Transaction> getTransactionList(UUID userId, Map<String, Object> filters) throws NotFoundException;

        /**
         * Retrieves the details of a specific transaction.
         *
         * @param transactionId The ID of the transaction to retrieve.
         * @param userId        The ID of the user owning the transaction (for
         *                      authorization).
         * @return The Transaction object.
         * @throws NotFoundException if the transaction or user is not found.
         * @throws SecurityException if the user is not authorized to access the
         *                           transaction.
         */
        Transaction getTransactionDetails(UUID transactionId, UUID userId) throws NotFoundException, SecurityException;

        /**
         * Creates a new transaction.
         * This operation should also update the balance of the associated account.
         *
         * @param userId      The ID of the user creating the transaction.
         * @param accountId   The ID of the account associated with the transaction.
         * @param categoryId  The ID of the category for the transaction.
         * @param amount      The amount of the transaction (positive for income,
         *                    negative for expense).
         * @param date        The date of the transaction.
         * @param description A description of the transaction.
         * @param type        The type of transaction ("income" or "expense").
         * @return The ID of the newly created transaction.
         * @throws ValidationException if the input data is invalid (e.g., amount is
         *                             zero, category doesn't match type).
         * @throws NotFoundException   if the user, account, or category is not found.
         * @throws SecurityException   if the user is not authorized to use the
         *                             account/category.
         */
        UUID createTransaction(UUID userId, UUID accountId, UUID categoryId, BigDecimal amount, LocalDate date,
                        String description, String type)
                        throws ValidationException, NotFoundException, SecurityException;

        /**
         * Updates the details of an existing transaction.
         * This operation might need to adjust account balances accordingly.
         *
         * @param transactionId The ID of the transaction to update.
         * @param userId        The ID of the user owning the transaction (for
         *                      authorization).
         * @param accountId     The new account ID (if changed).
         * @param categoryId    The new category ID.
         * @param amount        The new amount.
         * @param date          The new date.
         * @param description   The new description.
         * @return true if the update was successful, false otherwise.
         * @throws ValidationException if the input data is invalid.
         * @throws NotFoundException   if the transaction, user, account, or category is
         *                             not found.
         * @throws SecurityException   if the user is not authorized to update the
         *                             transaction or use the account/category.
         */
        boolean updateTransaction(UUID transactionId, UUID userId, UUID accountId, UUID categoryId, BigDecimal amount,
                        LocalDate date, String description)
                        throws ValidationException, NotFoundException, SecurityException;

        /**
         * Deletes an existing transaction.
         * This operation should also reverse the effect on the associated account
         * balance.
         *
         * @param transactionId The ID of the transaction to delete.
         * @param userId        The ID of the user owning the transaction (for
         *                      authorization).
         * @return true if the deletion was successful, false otherwise.
         * @throws NotFoundException if the transaction or user is not found.
         * @throws SecurityException if the user is not authorized to delete the
         *                           transaction.
         */
        boolean deleteTransaction(UUID transactionId, UUID userId) throws NotFoundException, SecurityException;
}
