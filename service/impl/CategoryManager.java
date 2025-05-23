package com.example.financetracker.service.impl;

import com.example.financetracker.domain.Category;
import com.example.financetracker.exception.NotFoundException;
import com.example.financetracker.exception.ValidationException;
import com.example.financetracker.service.interfaces.IManageCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of IManageCategory using in-memory storage.
 * NOTE: This assumes categories are global. If categories are user-specific,
 * the storage structure and method signatures need modification.
 * A real application would use a persistent data store.
 */
public class CategoryManager implements IManageCategory {

    // In-memory storage for categories (CategoryId -> Category)
    private final Map<UUID, Category> categories = new ConcurrentHashMap<>();
    // Add references to TransactionManager/BudgetManager if needed for 'in use' checks

    @Override
    public List<Category> getCategoryList() {
        // Return a copy to prevent external modification of the internal list
        return new ArrayList<>(categories.values());
    }

    @Override
    public Category getCategoryDetails(UUID categoryId) throws NotFoundException {
        Category category = categories.get(categoryId);
        if (category == null) {
            throw new NotFoundException("Category with ID " + categoryId + " not found.");
        }
        return category;
    }

    @Override
    public UUID createCategory(String name, String type, String icon) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name cannot be empty.");
        }
        if (type == null || !(type.equalsIgnoreCase("income") || type.equalsIgnoreCase("expense"))) {
            throw new ValidationException("Invalid category type: " + type + ". Must be 'income' or 'expense'.");
        }
        if (icon == null) {
             // Allow empty/null icon? Depends on requirements.
             // throw new ValidationException("Category icon cannot be null.");
             icon = ""; // Default to empty string if null
        }

        // Check for duplicates (same name and type)
        boolean exists = categories.values().stream()
                .anyMatch(cat -> cat.getName().equalsIgnoreCase(name.trim()) && cat.getType().equalsIgnoreCase(type));
        if (exists) {
            throw new ValidationException("A category with name '" + name.trim() + "' and type '" + type + "' already exists.");
        }

        Category newCategory = new Category(name.trim(), type, icon);
        categories.put(newCategory.getId(), newCategory);
        return newCategory.getId();
    }

    @Override
    public boolean updateCategory(UUID categoryId, String name, String type, String icon) throws ValidationException, NotFoundException {
        Category category = getCategoryDetails(categoryId); // Checks existence

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name cannot be empty.");
        }
        if (type == null || !(type.equalsIgnoreCase("income") || type.equalsIgnoreCase("expense"))) {
            throw new ValidationException("Invalid category type: " + type + ". Must be 'income' or 'expense'.");
        }
         if (icon == null) {
             icon = ""; // Default to empty string if null
        }

        // Check for duplicates if name or type changed
        if (!category.getName().equalsIgnoreCase(name.trim()) || !category.getType().equalsIgnoreCase(type)) {
            boolean exists = categories.values().stream()
                    .anyMatch(cat -> !cat.getId().equals(categoryId) && // Exclude self
                                   cat.getName().equalsIgnoreCase(name.trim()) && 
                                   cat.getType().equalsIgnoreCase(type));
            if (exists) {
                throw new ValidationException("Another category with name '" + name.trim() + "' and type '" + type + "' already exists.");
            }
        }

        category.setName(name.trim());
        category.setType(type);
        category.setIcon(icon);

        // In-memory update is automatic via object reference
        return true;
    }

    @Override
    public boolean deleteCategory(UUID categoryId) throws NotFoundException, ValidationException {
        Category category = getCategoryDetails(categoryId); // Checks existence

        // --- Check if category is in use (Requires dependency injection or access to other managers) ---
        // Example (Conceptual - requires actual Transaction/Budget data access):
        // if (transactionManager.isCategoryUsed(categoryId) || budgetManager.isCategoryUsed(categoryId)) {
        //     throw new ValidationException("Cannot delete category with ID " + categoryId + " because it is currently in use by transactions or budgets.");
        // }
        // For this basic implementation, we'll skip the 'in use' check.
        // System.out.println("Warning: Deleting category " + categoryId + " without checking if it's in use.");
        // --- End 'in use' check --- 

        categories.remove(categoryId);
        return true;
    }
}

