package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

public class UnknownCommand implements Command{
    private SmtpState state;

    public UnknownCommand(SmtpState state) {
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        return new SmtpResponse(503, "Not supported command", this.state);
    }
}