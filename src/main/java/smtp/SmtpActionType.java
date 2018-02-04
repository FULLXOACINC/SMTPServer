package smtp;

public enum SmtpActionType {
    CONNECT("Connect"), EHLO("EHLO"), MAIL("MAIL"), RCPT("RCPT"),
    DATA("DATA"), DATA_END("."), UNRECOG("Unknown"), QUIT("QUIT"),
    BLANK_LINE("Blank line"), RSET("RSET"),
    VRFY("VRFY"), EXPN("EXPN"), HELP("HELP"), NOOP("NOOP");

    private String value;

    SmtpActionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
