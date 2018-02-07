package smtp;

public enum SmtpActionType {

    DATA_END("."),
    BLANK_LINE("Blank line");

    private String value;
    private String description;

    SmtpActionType(String value) {
        this.value = value;
    }

    SmtpActionType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public String toString() {
        return value;
    }
}
