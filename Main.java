package com.example.financetracker;

import com.example.financetracker.domain.*;
import com.example.financetracker.exception.NotFoundException;
import com.example.financetracker.exception.ValidationException;
import com.example.financetracker.service.impl.*;
import com.example.financetracker.service.interfaces.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Main class to demonstrate the functionality of the Personal Finance Tracker.
 * This class initializes the services and performs some example operations.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("--- Personal Finance Tracker Demo ---");

        // --- Service Initialization (using in-memory implementations) ---
        // In a real application, these would be managed by a dependency injection framework (like Spring).
        IManageCategory categoryManager = new CategoryManager();
        IManageAccount accountManager = new AccountManager();
        // Cast to implementation to add user for demo purposes
        if (accountManager instanceof AccountManager) {
            ((AccountManager) accountManager).addUser(UUID.fromString("00000000-0000-0000-0000-000000000001")); // Add a demo user
        }
        IManageTransaction transactionManager = new TransactionManager(accountManager, categoryManager);
        IManageBudget budgetManager = new BudgetManager(categoryManager);
        IManageFinancialGoal financialGoalManager = new FinancialGoalManager();
        IGenerateReport reportGenerator = new ReportGenerator(transactionManager);

        // Add user to other managers that might need it for demo
        if (transactionManager instanceof TransactionManager) {
             ((TransactionManager) transactionManager).addUser(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        }
         if (budgetManager instanceof BudgetManager) {
             ((BudgetManager) budgetManager).addUser(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        }
         if (financialGoalManager instanceof FinancialGoalManager) {
             ((FinancialGoalManager) financialGoalManager).addUser(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        }
          if (reportGenerator instanceof ReportGenerator) {
             ((ReportGenerator) reportGenerator).addUser(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        }


        // --- Demo Operations ---
        UUID demoUserId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        try {
            System.out.println("\n1. Creating Account...");
            UUID savingsAccountId = accountManager.createAccount(demoUserId, "Savings Account", new BigDecimal("1000.00"), "Savings");
            System.out.println("   Account created with ID: " + savingsAccountId);
            Account savingsAccount = accountManager.getAccountDetails(savingsAccountId, demoUserId);
            System.out.println("   Account Details: " + savingsAccount);

            System.out.println("\n2. Creating Categories...");
            UUID salaryCategoryId = categoryManager.createCategory("Salary", "income", "💰");
            UUID foodCategoryId = categoryManager.createCategory("Food", "expense", "🍔");
            UUID transportCategoryId = categoryManager.createCategory("Transport", "expense", "🚗");
            System.out.println("   Salary Category ID: " + salaryCategoryId);
            System.out.println("   Food Category ID: " + foodCategoryId);
            System.out.println("   Transport Category ID: " + transportCategoryId);

            System.out.println("\n3. Recording Transactions...");
            UUID incomeTxId = transactionManager.createTransaction(demoUserId, savingsAccountId, salaryCategoryId, new BigDecimal("2500.00"), LocalDate.now(), "Monthly Salary", "income");
            System.out.println("   Income Transaction recorded. ID: " + incomeTxId);
            savingsAccount = accountManager.getAccountDetails(savingsAccountId, demoUserId); // Refresh account details
            System.out.println("   Account Balance after income: " + savingsAccount.getBalance());

            UUID expenseTxId1 = transactionManager.createTransaction(demoUserId, savingsAccountId, foodCategoryId, new BigDecimal("55.75"), LocalDate.now(), "Lunch", "expense");
            System.out.println("   Expense Transaction recorded. ID: " + expenseTxId1);
            UUID expenseTxId2 = transactionManager.createTransaction(demoUserId, savingsAccountId, transportCategoryId, new BigDecimal("30.00"), LocalDate.now(), "Gasoline", "expense");
            System.out.println("   Expense Transaction recorded. ID: " + expenseTxId2);

            savingsAccount = accountManager.getAccountDetails(savingsAccountId, demoUserId); // Refresh account details
            System.out.println("   Account Balance after expenses: " + savingsAccount.getBalance());

            System.out.println("\n4. Listing Transactions...");
            List<Transaction> transactions = transactionManager.getTransactionList(demoUserId, null); // Get all transactions
            System.out.println("   Total transactions found: " + transactions.size());
            transactions.forEach(tx -> System.out.println("   - " + tx));

            System.out.println("\n5. Creating Budget...");
            UUID foodBudgetId = budgetManager.createBudget(demoUserId, "Monthly Food Budget", new BigDecimal("400.00"), LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()), Collections.singletonList(foodCategoryId));
            System.out.println("   Food Budget created with ID: " + foodBudgetId);
            Budget foodBudget = budgetManager.getBudgetDetails(foodBudgetId, demoUserId);
            System.out.println("   Budget Details: " + foodBudget);

            System.out.println("\n6. Generating Report (Spending by Category)...");
            // Note: ReportGenerator currently returns Map<UUID, BigDecimal>
            // A real implementation would likely return a more structured object or use CategoryManager to resolve names.
            Map<UUID, BigDecimal> spendingReport = (Map<UUID, BigDecimal>) reportGenerator.generateReport(demoUserId, "spending_by_category", LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()), null);
            System.out.println("   Spending Report Data (CategoryID -> Amount):");
            spendingReport.forEach((catId, amount) -> {
                String categoryName = "Unknown";
                try {
                    categoryName = categoryManager.getCategoryDetails(catId).getName();
                } catch (NotFoundException e) { /* Ignore */ }
                System.out.println("     " + categoryName + " (" + catId + "): " + amount);
            });


        } catch (ValidationException | NotFoundException | SecurityException e) {
            System.err.println("\nError during demo execution: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\nAn unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- Demo Finished ---");
    }
}

