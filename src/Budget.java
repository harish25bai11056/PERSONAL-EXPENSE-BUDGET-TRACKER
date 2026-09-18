import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;

public class Budget {
    private final YearMonth month;
    private final Category category;
    private BigDecimal limit;

    public Budget(YearMonth month, Category category, BigDecimal limit) {
        this.month = Objects.requireNonNull(month, "Month is required");
        this.category = Objects.requireNonNull(category, "Category is required");
        this.limit = Objects.requireNonNull(limit, "Limit is required");
    }

    public YearMonth getMonth() { return month; }
    public Category getCategory() { return category; }
    public BigDecimal getLimit() { return limit; }
    public void setLimit(BigDecimal limit) { this.limit = Objects.requireNonNull(limit); }

    @Override
    public String toString() {
        return String.format("%s | %s | %s", month, category.getDisplayName(), limit);
    }
}
