package smtp;

public enum SmtpActionType {
    CONNECT("Connect"),
    EHLO("EHLO","Client start work with server"),
    MAIL("MAIL","Mail sending from ..."),
    RCPT("RCPT","Mail sending to..."),
    DATA("DATA","Send mail to sesver"),
    DATA_END("."),
    UNRECOG("Unknown"),
    QUIT("QUIT","Client end work with server"),
    BLANK_LINE("Blank line"),
    RSET("RSET","Removes all information about the client's communication with the server"),
    VRFY("VRFY","Check is server exist this client address"),
    EXPN("EXPN"),
    HELP("HELP","Return description of server command"),
    NOOP("NOOP","Return OK if server is work");

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
