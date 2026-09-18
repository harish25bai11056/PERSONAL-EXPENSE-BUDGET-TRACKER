import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Expense {
    private final int id;
    private BigDecimal amount;
    private Category category;
    private LocalDate date;
    private String description;

    public Expense(int id, BigDecimal amount, Category category, LocalDate date, String description) {
        this.id = id;
        this.amount = Objects.requireNonNull(amount, "Amount is required");
        this.category = Objects.requireNonNull(category, "Category is required");
        this.date = Objects.requireNonNull(date, "Date is required");
        this.description = Objects.requireNonNull(description, "Description is required");
    }

    public int getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public Category getCategory() { return category; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public void setAmount(BigDecimal amount) { this.amount = Objects.requireNonNull(amount); }
    public void setCategory(Category category) { this.category = Objects.requireNonNull(category); }
    public void setDate(LocalDate date) { this.date = Objects.requireNonNull(date); }
    public void setDescription(String description) { this.description = Objects.requireNonNull(description); }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s | %s | %s", id, date, category.getDisplayName(), amount, description);
    }
}
