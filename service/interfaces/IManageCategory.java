package service.interfaces;

import domain.Category;
import exception.NotFoundException;
import exception.ValidationException;

import java.util.List;
import java.util.UUID;

/**
 * Interface for managing transaction categories.
 * Provides operations for creating, retrieving, updating, and deleting
 * categories.
 */
public interface IManageCategory {

    /**
     * Retrieves a list of all categories available to a user (or globally,
     * depending on design).
     * Consider adding userId if categories are user-specific.
     *
     * @return A list of Category objects.
     */
    List<Category> getCategoryList(); // Potentially add userId parameter

    /**
     * Retrieves the details of a specific category.
     *
     * @param categoryId The ID of the category to retrieve.
     * @return The Category object.
     * @throws NotFoundException if the category is not found.
     */
    Category getCategoryDetails(UUID categoryId) throws NotFoundException;

    /**
     * Creates a new category.
     *
     * @param name The name of the new category.
     * @param type The type of the category ("income" or "expense").
     * @param icon An identifier for the category's icon.
     * @return The ID of the newly created category.
     * @throws ValidationException if the input data is invalid (e.g., name is
     *                             empty, type is invalid, name already exists for
     *                             the type).
     */
    UUID createCategory(String name, String type, String icon) throws ValidationException;

    /**
     * Updates the details of an existing category.
     *
     * @param categoryId The ID of the category to update.
     * @param name       The new name for the category.
     * @param type       The new type for the category.
     * @param icon       The new icon identifier.
     * @return true if the update was successful, false otherwise.
     * @throws ValidationException if the input data is invalid.
     * @throws NotFoundException   if the category is not found.
     */
    boolean updateCategory(UUID categoryId, String name, String type, String icon)
            throws ValidationException, NotFoundException;

    /**
     * Deletes an existing category.
     * Note: Consider implications if the category is used in transactions or
     * budgets.
     * The implementation might prevent deletion or require
     * confirmation/reassignment.
     *
     * @param categoryId The ID of the category to delete.
     * @return true if the deletion was successful, false otherwise.
     * @throws NotFoundException   if the category is not found.
     * @throws ValidationException if deletion is not allowed (e.g., category is in
     *                             use).
     */
    boolean deleteCategory(UUID categoryId) throws NotFoundException, ValidationException;
}
