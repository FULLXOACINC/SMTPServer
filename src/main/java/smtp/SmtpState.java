package smtp;

public enum SmtpState {
    CONNECT, GREET, MAIL, RCPT, DATA_BODY, DATA_HDR, QUIT
}