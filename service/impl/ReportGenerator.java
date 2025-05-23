package com.example.financetracker.service.impl;

import com.example.financetracker.domain.Transaction;
import com.example.financetracker.exception.NotFoundException;
import com.example.financetracker.exception.ValidationException;
import com.example.financetracker.service.interfaces.IGenerateReport;
import com.example.financetracker.service.interfaces.IManageTransaction;
// Import other managers as needed (CategoryManager, BudgetManager)

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of IGenerateReport using in-memory data processing.
 * NOTE: This is a basic implementation. Real-world reporting often involves
 * complex queries, data aggregation (potentially in a database), and dedicated reporting tools.
 * Assumes existence of TransactionManager and potentially other managers.
 */
public class ReportGenerator implements IGenerateReport {

    // Dependencies (Inject these)
    private final IManageTransaction transactionManager;
    // private final IManageCategory categoryManager; // Needed for category names
    // private final IManageBudget budgetManager; // Needed for budget comparisons
    // Simple user existence check (should be part of a dedicated user service)
    private final Map<UUID, Boolean> existingUsers = new ConcurrentHashMap<>(); // Should sync with other managers

    public ReportGenerator(IManageTransaction transactionManager /*, IManageCategory categoryManager, IManageBudget budgetManager */) {
        this.transactionManager = transactionManager;
        // this.categoryManager = categoryManager;
        // this.budgetManager = budgetManager;
        // Ideally, user existence is managed centrally
        if (transactionManager instanceof TransactionManager) {
             this.existingUsers.putAll(((TransactionManager) transactionManager).existingUsers);
        }
    }

     // Helper to simulate user existence (sync with other managers or use a central service)
    public void addUser(UUID userId) {
        existingUsers.put(userId, true);
         if (transactionManager instanceof TransactionManager) {
            ((TransactionManager) transactionManager).addUser(userId);
        }
        // Add to other managers if needed
    }

    private void checkUserExists(UUID userId) throws NotFoundException {
        if (!existingUsers.containsKey(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found.");
        }
    }

    @Override
    public Object generateReport(UUID userId, String reportType, LocalDate startDate, LocalDate endDate, Map<String, Object> parameters)
            throws ValidationException, NotFoundException {
        checkUserExists(userId);

        if (reportType == null || reportType.trim().isEmpty()) {
            throw new ValidationException("Report type cannot be empty.");
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new ValidationException("Invalid report period.");
        }

        // Prepare common filters
        Map<String, Object> filters = new HashMap<>();
        filters.put("startDate", startDate);
        filters.put("endDate", endDate);
        if (parameters != null) {
            filters.putAll(parameters); // Add specific filters like accountId, categoryId
        }

        // Fetch relevant transactions
        List<Transaction> transactions = transactionManager.getTransactionList(userId, filters);

        // Generate report based on type
        switch (reportType.toLowerCase()) {
            case "spending_by_category":
                return generateSpendingByCategoryReport(transactions);
            case "income_vs_expense_trend":
                return generateIncomeVsExpenseReport(transactions);
            // Add more report types here (e.g., budget variance, net worth)
            default:
                throw new ValidationException("Unsupported report type: " + reportType);
        }
    }

    private Map<UUID, BigDecimal> generateSpendingByCategoryReport(List<Transaction> transactions) {
        // Requires CategoryManager to get category names for a more user-friendly report
        return transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("expense"))
                .collect(Collectors.groupingBy(
                        Transaction::getCategoryId,
                        Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    private Map<String, BigDecimal> generateIncomeVsExpenseReport(List<Transaction> transactions) {
        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalIncome", BigDecimal.ZERO);
        summary.put("totalExpense", BigDecimal.ZERO);

        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase("income")) {
                summary.put("totalIncome", summary.get("totalIncome").add(t.getAmount()));
            } else if (t.getType().equalsIgnoreCase("expense")) {
                summary.put("totalExpense", summary.get("totalExpense").add(t.getAmount()));
            }
        }
        summary.put("netFlow", summary.get("totalIncome").subtract(summary.get("totalExpense")));
        return summary;
    }

    @Override
    public Object exportReport(UUID userId, String reportType, LocalDate startDate, LocalDate endDate, Map<String, Object> parameters, String format)
            throws ValidationException, NotFoundException, Exception {
        checkUserExists(userId);

        if (format == null || !(format.equalsIgnoreCase("CSV") || format.equalsIgnoreCase("PDF"))) {
            throw new ValidationException("Unsupported export format: " + format + ". Supported formats: CSV, PDF");
        }

        // Generate the report data first
        Object reportData = generateReport(userId, reportType, startDate, endDate, parameters);

        // --- Export Logic --- 
        // This part requires specific libraries for CSV/PDF generation (e.g., Apache Commons CSV, iText/OpenPDF)
        // For demonstration, we just return the raw data or a placeholder message.

        if (format.equalsIgnoreCase("CSV")) {
            // TODO: Implement CSV export logic using reportData
            System.out.println("CSV Export requested for report: " + reportType);
            return "CSV export not fully implemented. Data: " + reportData.toString(); // Placeholder
        } else if (format.equalsIgnoreCase("PDF")) {
            // TODO: Implement PDF export logic using reportData
            System.out.println("PDF Export requested for report: " + reportType);
            // Example using a simple text representation
             byte[] pdfContent = ("PDF Report: " + reportType + "\nPeriod: " + startDate + " to " + endDate + "\nData: " + reportData.toString()).getBytes();
             return pdfContent; // Placeholder returning byte array
            // return "PDF export not fully implemented. Data: " + reportData.toString(); // Placeholder string
        }

        return null; // Should not be reached
    }
}

