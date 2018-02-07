package smtp.command.impl;

import smtp.SmtpMail;
import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;
import validater.ClientNameValidator;

public class MailCommand implements Command {
    private static String TYPE = "post";
    private SmtpMail mail;
    private String params;
    private SmtpState state;

    public MailCommand(SmtpMail mail, String params, SmtpState state) {
        this.mail = mail;
        this.params = params;
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        if (SmtpState.MAIL != state) {
            new SmtpResponse(503, "Bad sequence of command ", this.state);
        }
        if (!ClientNameValidator.validateParams(params)) {
            return new SmtpResponse(504, "NOt valid parameter", this.state);
        }
        String client = params.substring(1, params.length() - 1);

        if (!ClientNameValidator.isClientExist(client)) {
            return new SmtpResponse(550, "Client not exist", this.state);
        }
        mail.setSendType(TYPE);
        mail.setSender(client);
        return new SmtpResponse(250, "OK", SmtpState.RCPT);

    }
}