# Personal Finance Tracker

## Developer Information

*   **Authors:**
    *   Muhammad Mahathir (2208107010056)
    *   Irfan Rizadi (2208107010062)
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



## How to Run

This project uses standard Java and does not require external build tools like Maven or Gradle for this basic setup. You can compile and run it using the Java Development Kit (JDK).

**Prerequisites:**
*   Java Development Kit (JDK) installed (version 8 or higher recommended).
*   Terminal or Command Prompt.

**Steps:**

1.  **Navigate to the Project Root:**
    Open your terminal or command prompt and navigate to the root directory of the project (`PersonalFinanceTracker`).

2.  **Compile the Java Files:**
    Compile all the `.java` files from the `src/main/java` directory. The compiled `.class` files will be placed relative to the specified output directory (we'll use a `bin` directory here).

    *   **On Linux/macOS:**
        ```bash
        mkdir -p bin
        find src/main/java -name "*.java" > sources.txt
        javac -d bin @sources.txt
        rm sources.txt
        ```
    *   **On Windows:**
        ```cmd
        mkdir bin
        dir src\main\java\*.java /s /b > sources.txt
        javac -d bin @sources.txt
        del sources.txt
        ```
    *(Note: The `find`/`dir` commands create a temporary file listing all source files, which `javac` then uses. This avoids issues with very long command lines.)*

3.  **Run the Main Class:**
    Execute the `Main` class from the `bin` directory using the `java` command. You need to specify the classpath (`-cp` or `-classpath`) so Java can find the compiled classes.

    *   **On Linux/macOS:**
        ```bash
        java -cp bin com.example.financetracker.Main
        ```
    *   **On Windows:**
        ```cmd
        java -cp bin com.example.financetracker.Main
        ```

4.  **Output:**
    You should see the output from the demo operations printed to your console, showing the creation of accounts, categories, transactions, and a simple report.

