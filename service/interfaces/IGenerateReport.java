package service.interfaces;

import exception.NotFoundException;
import exception.ValidationException;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for generating financial reports.
 * Provides operations to generate various types of reports based on user data.
 */
public interface IGenerateReport {

        /**
         * Generates a report based on the specified type and parameters.
         *
         * @param userId     The ID of the user for whom the report is generated.
         * @param reportType The type of report to generate (e.g.,
         *                   "spending_by_category", "income_vs_expense_trend").
         * @param startDate  The start date for the report period.
         * @param endDate    The end date for the report period.
         * @param parameters Additional parameters specific to the report type (e.g.,
         *                   accountId, categoryId).
         * @return A representation of the generated report (e.g., a Map, a custom
         *         Report object, or raw data for rendering).
         *         The exact return type depends on how reports are handled downstream.
         * @throws ValidationException if the report type or parameters are invalid.
         * @throws NotFoundException   if the user or related data (accounts,
         *                             categories) is not found.
         */
        Object generateReport(UUID userId, String reportType, LocalDate startDate, LocalDate endDate,
                        Map<String, Object> parameters)
                        throws ValidationException, NotFoundException;

        /**
         * Exports a generated report to a specified format.
         *
         * @param userId     The ID of the user.
         * @param reportType The type of report to export.
         * @param startDate  The start date for the report period.
         * @param endDate    The end date for the report period.
         * @param parameters Additional parameters.
         * @param format     The desired export format (e.g., "PDF", "CSV").
         * @return A byte array representing the exported file content, or a path to the
         *         generated file.
         *         The exact return type depends on the implementation strategy.
         * @throws ValidationException if the format is unsupported or parameters are
         *                             invalid.
         * @throws NotFoundException   if the user or data is not found.
         * @throws Exception           if there is an error during report generation or
         *                             file creation.
         */
        Object exportReport(UUID userId, String reportType, LocalDate startDate, LocalDate endDate,
                        Map<String, Object> parameters, String format)
                        throws ValidationException, NotFoundException, Exception;

}
