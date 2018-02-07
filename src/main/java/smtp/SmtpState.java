package smtp;

public enum SmtpState {
    CONNECT, GREET, MAIL, RCPT, DATA_BODY, DATA_HDR, DATA_END, QUIT
}