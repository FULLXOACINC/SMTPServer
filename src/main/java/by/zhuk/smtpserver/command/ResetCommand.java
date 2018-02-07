package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpMail;
import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

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