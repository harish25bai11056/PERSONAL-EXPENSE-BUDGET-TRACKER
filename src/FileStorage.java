import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public final class FileStorage {
    private FileStorage() { }

    public static List<Expense> loadExpenses(Path file) throws IOException {
        List<Expense> expenses = new ArrayList<>();
        if (!Files.exists(file)) return expenses;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                if (line.isBlank()) continue;
                List<String> fields = parseCsvLine(line);
                if (fields.size() != 5) throw new IOException("Malformed expense row: " + line);
                try {
                    expenses.add(new Expense(Integer.parseInt(fields.get(0)), new BigDecimal(fields.get(1)),
                            Category.fromDisplayName(fields.get(2)), LocalDate.parse(fields.get(3)), fields.get(4)));
                } catch (RuntimeException exception) {
                    throw new IOException("Malformed expense data: " + line, exception);
                }
            }
        }
        return expenses;
    }

    public static List<Budget> loadBudgets(Path file) throws IOException {
        List<Budget> budgets = new ArrayList<>();
        if (!Files.exists(file)) return budgets;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                if (line.isBlank()) continue;
                List<String> fields = parseCsvLine(line);
                if (fields.size() != 3) throw new IOException("Malformed budget row: " + line);
                try {
                    budgets.add(new Budget(YearMonth.parse(fields.get(0)), Category.fromDisplayName(fields.get(1)),
                            new BigDecimal(fields.get(2))));
                } catch (RuntimeException exception) {
                    throw new IOException("Malformed budget data: " + line, exception);
                }
            }
        }
        return budgets;
    }

    public static void saveExpenses(Path file, List<Expense> expenses) throws IOException {
        ensureParentDirectory(file);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("id,amount,category,date,description");
            writer.newLine();
            for (Expense expense : expenses) {
                writer.write(csv(expense.getId()) + "," + csv(expense.getAmount()) + "," +
                        csv(expense.getCategory().getDisplayName()) + "," + csv(expense.getDate()) + "," +
                        csv(expense.getDescription()));
                writer.newLine();
            }
        }
    }

    public static void saveBudgets(Path file, List<Budget> budgets) throws IOException {
        ensureParentDirectory(file);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("month,category,limit");
            writer.newLine();
            for (Budget budget : budgets) {
                writer.write(csv(budget.getMonth()) + "," + csv(budget.getCategory().getDisplayName()) + "," +
                        csv(budget.getLimit()));
                writer.newLine();
            }
        }
    }

    private static void ensureParentDirectory(Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
    }

    private static String csv(Object value) {
        String text = String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    private static List<String> parseCsvLine(String line) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        // Parse quoted fields so descriptions may safely contain commas.
        for (int index = 0; index < line.length(); index++) {
            char current = line.charAt(index);
            if (current == '"') {
                if (quoted && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    field.append('"'); index++;
                } else quoted = !quoted;
            } else if (current == ',' && !quoted) {
                fields.add(field.toString()); field.setLength(0);
            } else field.append(current);
        }
        if (quoted) throw new IOException("Unclosed quoted CSV field.");
        fields.add(field.toString());
        return fields;
    }
}
