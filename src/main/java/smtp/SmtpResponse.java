package smtp;

public class SmtpResponse {
    private int code;
    private String message;
    private SmtpState nextState;

    public SmtpResponse(int code, String message, SmtpState next) {
        this.code = code;
        this.message = message;
        this.nextState = next;
    }

    int getCode() {
        return code;
    }

    String getMessage() {
        return message;
    }

    SmtpState getNextState() {
        return nextState;
    }

    @Override
    public String toString() {
        return code + " " + message;
    }
}
