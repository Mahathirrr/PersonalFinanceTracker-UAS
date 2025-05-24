package service.interfaces;

import domain.Account;
import exception.NotFoundException;
import exception.ValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Interface for managing user accounts.
 * Provides operations for creating, retrieving, updating, and deleting
 * accounts.
 */
public interface IManageAccount {

    /**
     * Retrieves a list of all accounts for a specific user.
     *
     * @param userId The ID of the user whose accounts are to be retrieved.
     * @return A list of Account objects.
     * @throws NotFoundException if the user is not found.
     */
    List<Account> getAccountList(UUID userId) throws NotFoundException;

    /**
     * Retrieves the details of a specific account.
     *
     * @param accountId The ID of the account to retrieve.
     * @param userId    The ID of the user owning the account (for authorization).
     * @return The Account object.
     * @throws NotFoundException if the account or user is not found.
     * @throws SecurityException if the user is not authorized to access the
     *                           account.
     */
    Account getAccountDetails(UUID accountId, UUID userId) throws NotFoundException, SecurityException;

    /**
     * Creates a new account for a user.
     *
     * @param userId  The ID of the user creating the account.
     * @param name    The name of the new account.
     * @param balance The initial balance of the account.
     * @param type    The type of the account (e.g., Savings, Checking).
     * @return The ID of the newly created account.
     * @throws ValidationException if the input data is invalid (e.g., name is
     *                             empty).
     * @throws NotFoundException   if the user is not found.
     */
    UUID createAccount(UUID userId, String name, BigDecimal balance, String type)
            throws ValidationException, NotFoundException;

    /**
     * Updates the details of an existing account.
     *
     * @param accountId The ID of the account to update.
     * @param userId    The ID of the user owning the account (for authorization).
     * @param name      The new name for the account.
     * @param type      The new type for the account.
     * @param isActive  The new active status for the account.
     * @return true if the update was successful, false otherwise.
     * @throws ValidationException if the input data is invalid.
     * @throws NotFoundException   if the account or user is not found.
     * @throws SecurityException   if the user is not authorized to update the
     *                             account.
     */
    boolean updateAccount(UUID accountId, UUID userId, String name, String type, boolean isActive)
            throws ValidationException, NotFoundException, SecurityException;

    /**
     * Deletes an existing account.
     * Note: Consider implications if the account has associated transactions.
     * The implementation might prevent deletion or require confirmation.
     *
     * @param accountId The ID of the account to delete.
     * @param userId    The ID of the user owning the account (for authorization).
     * @return true if the deletion was successful, false otherwise.
     * @throws NotFoundException   if the account or user is not found.
     * @throws SecurityException   if the user is not authorized to delete the
     *                             account.
     * @throws ValidationException if deletion is not allowed (e.g., account has
     *                             transactions).
     */
    boolean deleteAccount(UUID accountId, UUID userId) throws NotFoundException, SecurityException, ValidationException;
}
