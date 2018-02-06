package smtp.command.impl;

import smtp.SmtpMail;
import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

public class ResetCommand implements Command {
    SmtpMail mail;

    public ResetCommand(SmtpMail mail) {
        this.mail=mail;
    }

    @Override
    public SmtpResponse execute() {
        mail.clear();
        return new SmtpResponse(250, "OK", SmtpState.GREET);
    }
}