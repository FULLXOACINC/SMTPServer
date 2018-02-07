package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

public class HelloCommand implements Command {
    private SmtpState state;

    public HelloCommand(SmtpState state) {
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {

        if (SmtpState.GREET == state) {
            return new SmtpResponse(250, "OK", SmtpState.MAIL);
        }
        return new SmtpResponse(503, "Bad sequence of command EHLO", this.state);
    }


}