public enum Category {
    FOOD("Food"),
    RENT("Rent"),
    UTILITIES("Utilities"),
    ENTERTAINMENT("Entertainment"),
    TRANSPORTATION("Transportation"),
    HEALTHCARE("Healthcare"),
    EDUCATION("Education"),
    SHOPPING("Shopping"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Category fromDisplayName(String value) {
        for (Category category : values()) {
            if (category.displayName.equalsIgnoreCase(value.trim())
                    || category.name().equalsIgnoreCase(value.trim())) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + value);
    }
}
