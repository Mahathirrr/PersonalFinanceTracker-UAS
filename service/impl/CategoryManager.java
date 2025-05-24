package service.impl;

// Import domain classes
import domain.Category;
// Import exception classes
import exception.NotFoundException;
import exception.ValidationException;
// Import interfaces (if used)
// import service.interfaces.IManageCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of CategoryManager using in-memory storage.
 * NOTE: Assumes categories are global.
 */
// public class CategoryManager implements IManageCategory {
public class CategoryManager {

    private final Map<UUID, Category> categories = new ConcurrentHashMap<>();

    // @Override
    public List<Category> getCategoryList() {
        return new ArrayList<>(categories.values());
    }

    // @Override
    // Renamed to getCategory to avoid conflict with getCategoryDetails from
    // interface if used
    public Category getCategory(UUID categoryId) throws NotFoundException {
        Category category = categories.get(categoryId);
        if (category == null) {
            throw new NotFoundException("Category with ID " + categoryId + " not found.");
        }
        return category;
    }

    // Helper method to find category by name (case-insensitive)
    public Category getCategoryByName(String name) throws NotFoundException {
        Optional<Category> found = categories.values().stream()
                .filter(cat -> cat.getName().equalsIgnoreCase(name))
                .findFirst();
        if (!found.isPresent()) {
            throw new NotFoundException("Category with name \"" + name + "\" not found.");
        }
        return found.get();
    }

    // @Override
    // Simplified createCategory, removing icon as it wasn't used in Main
    public Category createCategory(String name, String type) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name cannot be empty.");
        }
        if (type == null || !(type.equalsIgnoreCase("income") || type.equalsIgnoreCase("expense"))) {
            throw new ValidationException("Invalid category type: " + type + ". Must be 'income' or 'expense'.");
        }

        String trimmedName = name.trim();
        boolean exists = categories.values().stream()
                .anyMatch(cat -> cat.getName().equalsIgnoreCase(trimmedName) && cat.getType().equalsIgnoreCase(type));
        if (exists) {
            throw new ValidationException(
                    "A category with name '" + trimmedName + "' and type '" + type + "' already exists.");
        }

        Category newCategory = new Category(trimmedName, type, ""); // Pass empty string for icon
        categories.put(newCategory.getId(), newCategory);
        return newCategory; // Return the created object
    }

    // @Override
    // Simplified updateCategory
    public boolean updateCategory(UUID categoryId, String name, String type)
            throws ValidationException, NotFoundException {
        Category category = getCategory(categoryId); // Use the corrected getter

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name cannot be empty.");
        }
        if (type == null || !(type.equalsIgnoreCase("income") || type.equalsIgnoreCase("expense"))) {
            throw new ValidationException("Invalid category type: " + type + ". Must be 'income' or 'expense'.");
        }

        String trimmedName = name.trim();
        // Check for duplicates if name or type changed
        if (!category.getName().equalsIgnoreCase(trimmedName) || !category.getType().equalsIgnoreCase(type)) {
            boolean exists = categories.values().stream()
                    .anyMatch(cat -> !cat.getId().equals(categoryId) &&
                            cat.getName().equalsIgnoreCase(trimmedName) &&
                            cat.getType().equalsIgnoreCase(type));
            if (exists) {
                throw new ValidationException(
                        "Another category with name '" + trimmedName + "' and type '" + type + "' already exists.");
            }
        }

        category.setName(trimmedName);
        category.setType(type);
        // category.setIcon(""); // Assuming icon is not managed here

        return true;
    }

    // @Override
    public boolean deleteCategory(UUID categoryId) throws NotFoundException, ValidationException {
        Category category = getCategory(categoryId); // Checks existence

        // TODO: Add check if category is in use by transactions before deleting
        // For now, allow deletion

        categories.remove(categoryId);
        return true;
    }
}