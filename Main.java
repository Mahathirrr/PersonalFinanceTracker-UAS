import java.util.Map;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

// Import domain classes
import domain.Account;
import domain.Category; // Assuming Category exists in domain
import domain.Transaction;

// Import implementation classes directly from service.impl
import service.impl.AccountManager;
import service.impl.TransactionManager;
import service.impl.ReportGenerator;
import service.impl.CategoryManager; // Assuming CategoryManager exists in service.impl

// Import exceptions
import exception.NotFoundException;
import exception.ValidationException;

public class Main {
    public static void main(String[] args) {
        System.out.println("----- Personal Finance Tracker Demo -----");

        try {
            // Initialize managers using the concrete classes from service.impl
            AccountManager accountManager = new AccountManager();
            CategoryManager categoryManager = new CategoryManager(); // Instantiate CategoryManager
            // TransactionManager now depends on AccountManager and CategoryManager
            TransactionManager transactionManager = new TransactionManager(accountManager, categoryManager);
            // ReportGenerator depends on AccountManager and TransactionManager
            ReportGenerator reportGenerator = new ReportGenerator(accountManager, transactionManager);

            // --- Setup ---
            // Demo User ID
            UUID demoUserId = UUID.randomUUID();
            accountManager.addUser(demoUserId); // Ensure user exists in AccountManager
            // categoryManager.addUser(demoUserId); // Add user to CategoryManager if it manages users

            // Create some categories first
            System.out.println("\nCreating Categories...");
            Category catIncome = categoryManager.createCategory("Salary", "Income");
            Category catFood = categoryManager.createCategory("Food", "Expense");
            Category catUtilities = categoryManager.createCategory("Utilities", "Expense");
            Category catEntertainment = categoryManager.createCategory("Entertainment", "Expense");
            Category catShopping = categoryManager.createCategory("Shopping", "Expense");
            System.out.println("Categories Created.");

            // Create some accounts for the demo user
            System.out.println("\nCreating Accounts...");
            Account checkingAccount = accountManager.createAccount(demoUserId, "Checking Account", BigDecimal.valueOf(1000), "Checking");
            Account savingsAccount = accountManager.createAccount(demoUserId, "Savings Account", BigDecimal.valueOf(5000), "Savings");
            System.out.println("Accounts Created.");

            // Display initial account balances
            System.out.println("\nInitial Balances:");
            System.out.println(accountManager.getAccount(checkingAccount.getId(), demoUserId));
            System.out.println(accountManager.getAccount(savingsAccount.getId(), demoUserId));
            System.out.println("-----");

            // --- Record Transactions ---
            System.out.println("\nRecording Transactions...");
            // Use category names now for recordTransaction
            // Income
            transactionManager.recordTransaction(demoUserId, checkingAccount.getId(), catIncome.getName(), "Income", BigDecimal.valueOf(2000), LocalDate.now());
            // Expenses
            transactionManager.recordTransaction(demoUserId, checkingAccount.getId(), catFood.getName(), "Expense", BigDecimal.valueOf(150), LocalDate.now().minusDays(5)); // Groceries
            transactionManager.recordTransaction(demoUserId, checkingAccount.getId(), catFood.getName(), "Expense", BigDecimal.valueOf(75), LocalDate.now().minusDays(3)); // Dinner Out
            transactionManager.recordTransaction(demoUserId, checkingAccount.getId(), catUtilities.getName(), "Expense", BigDecimal.valueOf(100), LocalDate.now().minusDays(10)); // Gas Bill
            transactionManager.recordTransaction(demoUserId, checkingAccount.getId(), catEntertainment.getName(), "Expense", BigDecimal.valueOf(40), LocalDate.now().minusDays(1)); // Movie Tickets
            transactionManager.recordTransaction(demoUserId, savingsAccount.getId(), catShopping.getName(), "Expense", BigDecimal.valueOf(200), LocalDate.now().minusDays(7)); // Online Shopping
            System.out.println("Transactions Recorded.");

            // --- Display Data ---
            // Display updated account balances
            System.out.println("\nBalances after transactions:");
            System.out.println(accountManager.getAccount(checkingAccount.getId(), demoUserId));
            System.out.println(accountManager.getAccount(savingsAccount.getId(), demoUserId));
            System.out.println("-----");

            // Display transactions for the checking account
            System.out.println("\nTransactions for Checking Account:");
            for (Transaction t : transactionManager.getTransactionsByAccount(checkingAccount.getId())) {
                System.out.println(t);
            }
            System.out.println("-----");

            // Display transactions for the demo user within a date range
            System.out.println("\nUser Transactions This Month:");
            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            for (Transaction t : transactionManager.getTransactionsByUser(demoUserId, startOfMonth, endOfMonth)) {
                System.out.println(t);
            }
            System.out.println("-----");

            // --- Generate Reports ---
            System.out.println("\nGenerating Reports...");

            // Spending by Category Report
            System.out.println("\nSpending by Category Report (This Month):");
            // ReportGenerator now returns Map<String, BigDecimal> for spending_by_category
            Map<String, BigDecimal> spendingReport = (Map<String, BigDecimal>) reportGenerator.generateReport(demoUserId, "spending_by_category", startOfMonth, endOfMonth, null);
            if (spendingReport != null && !spendingReport.isEmpty()) {
                spendingReport.forEach((categoryName, amount) -> {
                    // Amount is negative for expenses, display absolute value
                    System.out.println("Category: " + categoryName + ", Amount Spent: " + amount.abs());
                });
            } else {
                System.out.println("No spending data for this period or report failed.");
            }
            System.out.println("-----");

            // Income vs Expense Report
            System.out.println("\nIncome vs Expense Report (This Month):");
            Map<String, BigDecimal> incomeExpenseReport = (Map<String, BigDecimal>) reportGenerator.generateReport(demoUserId, "income_vs_expense", startOfMonth, endOfMonth, null);
            if (incomeExpenseReport != null) {
                 System.out.println("Total Income: " + incomeExpenseReport.getOrDefault("totalIncome", BigDecimal.ZERO));
                 System.out.println("Total Expense: " + incomeExpenseReport.getOrDefault("totalExpense", BigDecimal.ZERO));
                 System.out.println("Net Flow: " + incomeExpenseReport.getOrDefault("netFlow", BigDecimal.ZERO));
            } else {
                 System.out.println("Could not generate income vs expense report.");
            }
            System.out.println("-----");

        } catch (NotFoundException | ValidationException e) {
            System.err.println("\n*** Error during demo execution: " + e.getMessage());
            // e.printStackTrace(); // Uncomment for full stack trace
        } catch (Exception e) {
            System.err.println("\n*** An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n----- End of Demo -----");
    }
}