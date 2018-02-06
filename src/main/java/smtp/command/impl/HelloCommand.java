package smtp.command.impl;

import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;
import validater.ClientNameValidator;

import java.util.regex.Pattern;

public class HelloCommand implements Command {
    private SmtpState state;
    private String params;

    public HelloCommand(SmtpState state, String params) {
        this.state = state;
        this.params = params;
    }

    @Override
    public SmtpResponse execute() {

        if (!ClientNameValidator.validateParams(params)) {
            return new SmtpResponse(503, "NOt valid parameter", this.state);
        }
        String client = params.substring(1, params.length() - 1);

        if (!ClientNameValidator.isClientExist(client)) {
            return new SmtpResponse(550, "Client not exist", this.state);
        }

        if (SmtpState.GREET == state) {
            return new SmtpResponse(250, "OK", SmtpState.MAIL);
        }
        return new SmtpResponse(503, "Bad sequence of command EHLO", this.state);
    }


}