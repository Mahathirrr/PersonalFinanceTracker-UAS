package service.impl;

import domain.Transaction;
import exception.NotFoundException;
import exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ReportGenerator using in-memory data processing.
 * NOTE: Renamed from ReportGeneratorImpl based on user feedback.
 */
// Assuming ReportGenerator interface exists in service package
// If not, this class should just be ReportGenerator without implements
public class ReportGenerator /* implements service.ReportGenerator */ {

    // Use the concrete classes from service.impl directly
    private final AccountManager accountManager;
    private final TransactionManager transactionManager;
    // No separate user tracking needed, rely on AccountManager

    public ReportGenerator(AccountManager accountManager, TransactionManager transactionManager) {
        this.accountManager = accountManager;
        this.transactionManager = transactionManager;
    }

    // Rely on AccountManager for user existence check
    private void checkUserExists(UUID userId) throws NotFoundException {
        if (!accountManager.userExists(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found.");
        }
    }

    // Method signature might need to match an interface if one exists
    // @Override
    public Object generateReport(UUID userId, String reportType, LocalDate startDate, LocalDate endDate,
            Map<String, Object> parameters)
            throws ValidationException, NotFoundException {
        checkUserExists(userId);

        if (reportType == null || reportType.trim().isEmpty()) {
            throw new ValidationException("Report type cannot be empty.");
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new ValidationException("Invalid report period.");
        }

        // Fetch relevant transactions using TransactionManager
        List<Transaction> transactions = transactionManager.getTransactionsByUser(userId, startDate, endDate);

        // Generate report based on type
        switch (reportType.toLowerCase()) {
            case "spending_by_category":
                return generateSpendingByCategoryReport(transactions);
            case "income_vs_expense":
                return generateIncomeVsExpenseReport(transactions);
            // Add more report types here
            default:
                throw new ValidationException("Unsupported report type: " + reportType);
        }
    }

    private Map<String, BigDecimal> generateSpendingByCategoryReport(List<Transaction> transactions) {
        // Group expenses by category description (which is stored in transaction
        // description for now)
        return transactions.stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) < 0) // Filter for expenses (negative amounts)
                .collect(Collectors.groupingBy(
                        Transaction::getDescription, // Group by description (acting as category name)
                        Collectors.mapping(Transaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
    }

    private Map<String, BigDecimal> generateIncomeVsExpenseReport(List<Transaction> transactions) {
        Map<String, BigDecimal> summary = new HashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.getAmount().compareTo(BigDecimal.ZERO) > 0) { // Income
                totalIncome = totalIncome.add(t.getAmount());
            } else { // Expense
                totalExpense = totalExpense.add(t.getAmount().abs()); // Add absolute value for total expense
            }
        }
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("netFlow", totalIncome.subtract(totalExpense));
        return summary;
    }

    // Export functionality might be better in a separate service
    // Method signature might need to match an interface if one exists
    // @Override
    public Object exportReport(UUID userId, String reportType, LocalDate startDate, LocalDate endDate,
            Map<String, Object> parameters, String format)
            throws ValidationException, NotFoundException, Exception {
        checkUserExists(userId);

        if (format == null || !(format.equalsIgnoreCase("CSV") || format.equalsIgnoreCase("PDF"))) {
            throw new ValidationException("Unsupported export format: " + format + ". Supported formats: CSV, PDF");
        }

        Object reportData = generateReport(userId, reportType, startDate, endDate, parameters);

        // Placeholder for export logic
        if (format.equalsIgnoreCase("CSV")) {
            return "CSV Export for " + reportType + ": " + reportData.toString();
        } else { // PDF
            byte[] pdfContent = ("PDF Report: " + reportType + "\nData: " + reportData.toString()).getBytes();
            return pdfContent;
        }
    }
}