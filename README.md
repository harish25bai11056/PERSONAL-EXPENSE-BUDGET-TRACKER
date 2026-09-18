# Personal Expense & Budget Tracker

A pure Java 17 command-line application for recording personal expenses, managing monthly category budgets, and generating spending summaries. Data is stored locally in CSV files, so the application works without a database or external dependency.

## Key Features

- Expense entry manager: add, view, edit, and delete records
- Category and month filters
- Monthly budget limits by category
- Alerts at 80% of a budget and when a budget is exceeded
- Monthly totals and category-level analytics
- Validated input and descriptive error messages
- Automatic CSV persistence in the `data` directory

## Technical Stack & Prerequisites

- Java Development Kit (JDK) 17 or newer
- Visual Studio Code
- Extension Pack for Java from Microsoft
- No external libraries or build tools are required

## Directory Structure

```text
Personal Expense Tracker/
├── src/
│   ├── Budget.java
│   ├── Category.java
│   ├── Expense.java
│   ├── ExpenseService.java
│   ├── FileStorage.java
│   ├── Main.java
│   └── ValidationUtils.java
├── data/                 # Created automatically at runtime
├── README.md
├── statement.md
└── report-diagrams.md
```

## Set Up, Compile, and Run in VS Code

1. Install JDK 17 or newer and confirm it is available with `java --version`.
2. Install the VS Code Extension Pack for Java.
3. Open the `Personal Expense Tracker` folder in VS Code.
4. Open the integrated terminal with **Terminal > New Terminal**.
5. Compile the source files:

   ```powershell
   javac -d out src\*.java
   ```

6. Run the application:

   ```powershell
   java -cp out Main
   ```

7. The first save creates `data\expenses.csv` and `data\budgets.csv`. Keep these files with the project to preserve data.

Alternatively, open `src\Main.java` and use the Java Run button provided by the extension.

## Sample Console Workflows

### Add an expense

```text
Choose an option: 1
Amount: 45.50
Category: Food
Date (yyyy-MM-dd): 2026-09-18
Description: Weekly groceries
Added expense #1 | 2026-09-18 | Food | 45.50 | Weekly groceries
```

### Set a budget and view an alert

```text
Choose an option: 5
Month (yyyy-MM): 2026-09
Category: Food
Monthly limit: 200
Budget saved: 2026-09 / Food / 200

Choose an option: 6
Month (yyyy-MM): 2026-09
No budget alerts for 2026-09.
```

When spending reaches 80% of the configured limit, the application displays a warning. Spending above the limit is reported as an exceeded budget.
