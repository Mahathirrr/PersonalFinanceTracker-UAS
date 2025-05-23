# Personal Finance Tracker

## Developer Information

*   **Author:** Muhammad Mahathir (2208107010056)
*   **Course:** Component-Based Software
*   **Lecturer:** Kurnia Saputra, S.T., M.Sc.

## Project Description

Personal Finance Tracker is a Java-based application designed to help users effectively track and manage their personal finances. This application was built as part of the Component-Based Software course assignment, focusing on separation of concerns through the use of components (domain, service interface, service implementation).

The application allows users to:
*   Record income and expenses.
*   Categorize each transaction.
*   Create and manage monthly or periodic budgets.
*   Set and track the progress of financial goals (e.g., saving for a vacation, purchasing an item).
*   View simple financial summaries and reports.

## Key Features

Based on requirements analysis and component design, the key implemented features include:

1.  **Account Management:** Create, view, update, and delete financial accounts (e.g., bank accounts, digital wallets, credit cards).
2.  **Transaction Logging:** Record income and expense transactions associated with specific accounts and classify them into categories.
3.  **Category Management:** Create, view, update, and delete transaction categories (e.g., Salary, Food, Transportation, Entertainment).
4.  **Budget Management:** Create, view, update, and delete budgets for specific expense categories over a defined period.
5.  **Financial Goal Management:** Create, view, update, delete, and track the progress of personal financial goals.
6.  **Report Generation:** Generate basic reports such as spending summaries by category or comparison of total income versus expenses.

## Project Structure

The project follows a standard Maven/Gradle directory structure with logical separation as follows:

*   `com.example.financetracker.domain`: Contains entity classes (POJOs) representing core data (User, Account, Transaction, Category, Budget, FinancialGoal).
*   `com.example.financetracker.exception`: Contains custom exception classes for specific error handling (NotFoundException, ValidationException).
*   `com.example.financetracker.service.interfaces`: Contains interfaces defining the contracts for each business service (IManageAccount, IManageTransaction, etc.).
*   `com.example.financetracker.service.impl`: Contains concrete implementation classes of the service interfaces, using in-memory data storage for demonstration purposes.

## Technology

*   Java
*   In-Memory Data Storage (Basic implementation using Maps)

