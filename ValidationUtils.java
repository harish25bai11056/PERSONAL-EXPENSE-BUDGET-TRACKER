import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.math.RoundingMode;

public final class ValidationUtils {
    private ValidationUtils() { }

    public static BigDecimal requirePositiveAmount(String value, String fieldName) throws InputValidationException {
        try {
            BigDecimal amount = new BigDecimal(value.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InputValidationException(fieldName + " must be greater than zero.");
            }
            return amount.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException exception) {
            throw new InputValidationException(fieldName + " must be a valid number.");
        }
    }

    public static LocalDate requireDate(String value) throws InputValidationException {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new InputValidationException("Date must use yyyy-MM-dd format.");
        }
    }

    public static YearMonth requireMonth(String value) throws InputValidationException {
        try {
            return YearMonth.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new InputValidationException("Month must use yyyy-MM format.");
        }
    }

    public static int requireId(String value) throws InputValidationException {
        try {
            int id = Integer.parseInt(value.trim());
            if (id <= 0) {
                throw new InputValidationException("ID must be positive.");
            }
            return id;
        } catch (NumberFormatException exception) {
            throw new InputValidationException("ID must be a whole number.");
        }
    }

    public static String requireText(String value, String fieldName) throws InputValidationException {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw new InputValidationException(fieldName + " cannot be empty.");
        }
        return trimmed;
    }

    public static Category requireCategory(String value) throws InputValidationException {
        try {
            return Category.fromDisplayName(value);
        } catch (IllegalArgumentException exception) {
            throw new InputValidationException("Unknown category. Choose one from the displayed list.");
        }
    }

    public static final class InputValidationException extends Exception {
        public InputValidationException(String message) { super(message); }
    }
}
