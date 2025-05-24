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

The project follows a logical component-based structure with clear separation of concerns:

*   `domain/`: Contains entity classes (POJOs) representing core data (User, Account, Transaction, Category, Budget, FinancialGoal).
*   `exception/`: Contains custom exception classes for specific error handling (NotFoundException, ValidationException).
*   `service/interfaces/`: Contains interfaces defining the contracts for each business service (IManageAccount, IManageTransaction, etc.).
*   `service/impl/`: Contains concrete implementation classes of the service interfaces, using in-memory data storage for demonstration purposes.
*   `bin/`: Contains compiled `.class` files organized in the same structure as the source files.

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
    Open your terminal or command prompt and navigate to the root directory of the project (where `Main.java` is located).

2.  **Compile the Java Files:**
    Compile all the `.java` files in the current directory and subdirectories. The compiled `.class` files will be placed in the `bin` directory.

    *   **On Linux/macOS:**
        ```bash
        # Create bin directory if it doesn't exist
        mkdir -p bin
        
        # Find all Java files and compile them
        find . -name "*.java" -not -path "./bin/*" > sources.txt
        javac -d bin @sources.txt
        rm sources.txt
        ```
    *   **On Windows:**
        ```cmd
        REM Create bin directory if it doesn't exist
        if not exist bin mkdir bin
        
        REM Find all Java files and compile them
        dir *.java /s /b | findstr /v "bin" > sources.txt
        javac -d bin @sources.txt
        del sources.txt
        ```

3.  **Run the Main Class:**
    Execute the `Main` class from the `bin` directory using the `java` command. You need to specify the classpath (`-cp` or `-classpath`) so Java can find the compiled classes.

    *   **On Linux/macOS:**
        ```bash
        java -cp bin Main
        ```
    *   **On Windows:**
        ```cmd
        java -cp bin Main
        ```

4.  **Alternative Quick Compilation (if bin directory already exists):**
    If you already have the `bin` directory and just want to recompile:

    *   **On Linux/macOS:**
        ```bash
        javac -d bin *.java */*.java */*/*.java
        java -cp bin Main
        ```
    *   **On Windows:**
        ```cmd
        javac -d bin *.java *\*.java *\*\*.java
        java -cp bin Main
        ```

5.  **Output:**
    You should see the output from the demo operations printed to your console, showing the creation of accounts, categories, transactions, and a simple report.

## Project Structure Tree
```
.
├── bin/                    # Compiled .class files
│   ├── domain/
│   ├── exception/
│   ├── service/
│   │   ├── impl/
│   │   └── interfaces/
│   └── Main.class
├── domain/                 # Entity classes
├── exception/              # Custom exceptions
├── service/                # Business logic
│   ├── impl/              # Service implementations
│   └── interfaces/        # Service interfaces
├── Main.java              # Application entry point
└── README.md
```