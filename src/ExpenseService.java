import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExpenseService {
    private final List<Expense> expenses = new ArrayList<>();
    private final Map<String, Budget> budgets = new java.util.HashMap<>();
    private int nextId = 1;

    public void loadData(List<Expense> storedExpenses, List<Budget> storedBudgets) {
        expenses.clear(); expenses.addAll(storedExpenses);
        budgets.clear();
        for (Budget budget : storedBudgets) budgets.put(budgetKey(budget.getMonth(), budget.getCategory()), budget);
        nextId = expenses.stream().mapToInt(Expense::getId).max().orElse(0) + 1;
    }

    public Expense addExpense(BigDecimal amount, Category category, java.time.LocalDate date, String description) {
        Expense expense = new Expense(nextId++, amount, category, date, description);
        expenses.add(expense);
        return expense;
    }

    public boolean updateExpense(int id, BigDecimal amount, Category category, java.time.LocalDate date, String description) {
        Optional<Expense> match = findById(id);
        if (match.isEmpty()) return false;
        Expense expense = match.get();
        expense.setAmount(amount); expense.setCategory(category); expense.setDate(date); expense.setDescription(description);
        return true;
    }

    public boolean deleteExpense(int id) { return expenses.removeIf(expense -> expense.getId() == id); }
    public Optional<Expense> findById(int id) { return expenses.stream().filter(e -> e.getId() == id).findFirst(); }
    public List<Expense> getAllExpenses() { return expenses.stream().sorted(Comparator.comparing(Expense::getDate).reversed()).toList(); }
    public List<Expense> filterByMonth(YearMonth month) { return expenses.stream().filter(e -> YearMonth.from(e.getDate()).equals(month)).sorted(Comparator.comparing(Expense::getDate)).toList(); }
    public List<Expense> filterByCategory(Category category) { return expenses.stream().filter(e -> e.getCategory() == category).sorted(Comparator.comparing(Expense::getDate).reversed()).toList(); }

    public BigDecimal totalSpending() { return totalOf(expenses); }
    public BigDecimal totalForMonth(YearMonth month) { return totalOf(filterByMonth(month)); }
    public Map<Category, BigDecimal> spendingByCategory(YearMonth month) {
        Map<Category, BigDecimal> result = new EnumMap<>(Category.class);
        for (Expense expense : filterByMonth(month)) result.merge(expense.getCategory(), expense.getAmount(), BigDecimal::add);
        return result;
    }

    public void setBudget(YearMonth month, Category category, BigDecimal limit) { budgets.put(budgetKey(month, category), new Budget(month, category, limit)); }
    public List<Budget> getBudgets() { return budgets.values().stream().sorted(Comparator.comparing(Budget::getMonth).thenComparing(Budget::getCategory)).toList(); }
    public List<String> budgetAlerts(YearMonth month) {
        List<String> alerts = new ArrayList<>();
        for (Budget budget : budgets.values()) {
            if (!budget.getMonth().equals(month)) continue;
            BigDecimal spent = spendingByCategory(month).getOrDefault(budget.getCategory(), BigDecimal.ZERO);
            if (spent.compareTo(budget.getLimit()) > 0) alerts.add(budget.getCategory().getDisplayName() + " exceeded by " + spent.subtract(budget.getLimit()));
            else if (spent.compareTo(budget.getLimit().multiply(new BigDecimal("0.8"))) >= 0) alerts.add(budget.getCategory().getDisplayName() + " reached " + spent + " of " + budget.getLimit());
        }
        return alerts;
    }

    public List<Expense> expenseSnapshot() { return new ArrayList<>(expenses); }
    public List<Budget> budgetSnapshot() { return new ArrayList<>(budgets.values()); }
    private BigDecimal totalOf(List<Expense> items) { return items.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add); }
    private String budgetKey(YearMonth month, Category category) { return month + "|" + category.name(); }
}
