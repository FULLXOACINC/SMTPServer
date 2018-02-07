package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

public class NoopCommand implements Command {
    private SmtpState state;

    public NoopCommand(SmtpState state) {
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        return new SmtpResponse(250, "OK", state);
    }
}