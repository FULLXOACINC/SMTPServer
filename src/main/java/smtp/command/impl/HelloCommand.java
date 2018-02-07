package smtp.command.impl;

import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;
import validater.ClientNameValidator;

import java.util.regex.Pattern;

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