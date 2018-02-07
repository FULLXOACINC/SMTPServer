package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

public class ConnectCommand implements Command {
    private final SmtpState state;

    public ConnectCommand(SmtpState state) {
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        if (SmtpState.CONNECT == state) {
            return new SmtpResponse(220, "localhost SMTP service ready", SmtpState.GREET);
        } else {
            return new SmtpResponse(503, "Bad sequence of command", this.state);
        }
    }
}