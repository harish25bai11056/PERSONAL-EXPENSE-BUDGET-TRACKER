import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Path DATA_DIRECTORY = Path.of("data");
    private static final Path EXPENSE_FILE = DATA_DIRECTORY.resolve("expenses.csv");
    private static final Path BUDGET_FILE = DATA_DIRECTORY.resolve("budgets.csv");
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final ExpenseService SERVICE = new ExpenseService();

    public static void main(String[] args) {
        try {
            SERVICE.loadData(FileStorage.loadExpenses(EXPENSE_FILE), FileStorage.loadBudgets(BUDGET_FILE));
            runMenu();
        } catch (IOException exception) {
            System.err.println("Unable to load data: " + exception.getMessage());
        } finally {
            SCANNER.close();
        }
    }

    private static void runMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Personal Expense & Budget Tracker ===");
            System.out.println("1. Add expense\n2. View expenses\n3. Edit expense\n4. Delete expense");
            System.out.println("5. Set monthly budget\n6. View budget alerts\n7. View analytics\n0. Exit");
            String choice = prompt("Choose an option: ");
            try {
                switch (choice) {
                    case "1" -> addExpense(); case "2" -> viewExpenses(); case "3" -> editExpense(); case "4" -> deleteExpense();
                    case "5" -> setBudget(); case "6" -> viewAlerts(); case "7" -> showAnalytics();
                    case "0" -> { saveData(); System.out.println("Data saved. Goodbye!"); running = false; }
                    default -> System.out.println("Please choose a listed option.");
                }
            } catch (ValidationUtils.InputValidationException exception) { System.out.println("Input error: " + exception.getMessage()); }
              catch (IOException exception) { System.out.println("Storage error: " + exception.getMessage()); }
        }
    }

    private static void addExpense() throws ValidationUtils.InputValidationException, IOException {
        BigDecimal amount = ValidationUtils.requirePositiveAmount(prompt("Amount: "), "Amount");
        Category category = chooseCategory(); LocalDate date = ValidationUtils.requireDate(prompt("Date (yyyy-MM-dd): "));
        String description = ValidationUtils.requireText(prompt("Description: "), "Description");
        Expense expense = SERVICE.addExpense(amount, category, date, description); saveData();
        System.out.println("Added expense " + expense); printAlerts(YearMonth.from(date));
    }

    private static void viewExpenses() throws ValidationUtils.InputValidationException {
        String filter = prompt("View (A)ll, (M)onth, or (C)ategory: ").toUpperCase();
        List<Expense> results = filter.equals("M") ? SERVICE.filterByMonth(ValidationUtils.requireMonth(prompt("Month (yyyy-MM): "))) :
                filter.equals("C") ? SERVICE.filterByCategory(chooseCategory()) : SERVICE.getAllExpenses();
        if (results.isEmpty()) System.out.println("No expenses found."); else results.forEach(System.out::println);
    }

    private static void editExpense() throws ValidationUtils.InputValidationException, IOException {
        int id = ValidationUtils.requireId(prompt("Expense ID: "));
        if (SERVICE.findById(id).isEmpty()) { System.out.println("Expense not found."); return; }
        BigDecimal amount = ValidationUtils.requirePositiveAmount(prompt("New amount: "), "Amount"); Category category = chooseCategory();
        LocalDate date = ValidationUtils.requireDate(prompt("New date (yyyy-MM-dd): ")); String description = ValidationUtils.requireText(prompt("New description: "), "Description");
        SERVICE.updateExpense(id, amount, category, date, description); saveData(); System.out.println("Expense updated.");
    }

    private static void deleteExpense() throws ValidationUtils.InputValidationException, IOException {
        int id = ValidationUtils.requireId(prompt("Expense ID: "));
        if (SERVICE.deleteExpense(id)) { saveData(); System.out.println("Expense deleted."); } else System.out.println("Expense not found.");
    }

    private static void setBudget() throws ValidationUtils.InputValidationException, IOException {
        YearMonth month = ValidationUtils.requireMonth(prompt("Month (yyyy-MM): ")); Category category = chooseCategory();
        BigDecimal limit = ValidationUtils.requirePositiveAmount(prompt("Monthly limit: "), "Limit"); SERVICE.setBudget(month, category, limit); saveData();
        System.out.println("Budget saved: " + month + " / " + category.getDisplayName() + " / " + limit);
    }

    private static void viewAlerts() throws ValidationUtils.InputValidationException { printAlerts(ValidationUtils.requireMonth(prompt("Month (yyyy-MM): "))); }
    private static void printAlerts(YearMonth month) { List<String> alerts = SERVICE.budgetAlerts(month); if (alerts.isEmpty()) System.out.println("No budget alerts for " + month + "."); else alerts.forEach(alert -> System.out.println("ALERT: " + alert)); }

    private static void showAnalytics() throws ValidationUtils.InputValidationException {
        YearMonth month = ValidationUtils.requireMonth(prompt("Month (yyyy-MM): ")); System.out.println("\nAnalytics for " + month);
        System.out.println("Total: " + SERVICE.totalForMonth(month));
        for (Map.Entry<Category, BigDecimal> entry : SERVICE.spendingByCategory(month).entrySet()) System.out.println(entry.getKey().getDisplayName() + ": " + entry.getValue());
        printAlerts(month);
    }

    private static Category chooseCategory() throws ValidationUtils.InputValidationException {
        System.out.println("Categories: "); for (Category category : Category.values()) System.out.println("- " + category.getDisplayName());
        return ValidationUtils.requireCategory(prompt("Category: "));
    }

    private static String prompt(String message) { System.out.print(message); return SCANNER.nextLine(); }
    private static void saveData() throws IOException { FileStorage.saveExpenses(EXPENSE_FILE, SERVICE.expenseSnapshot()); FileStorage.saveBudgets(BUDGET_FILE, SERVICE.budgetSnapshot()); }
}
