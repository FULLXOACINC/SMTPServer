package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpMail;
import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

public class ReadInformationCommand implements Command {
    private SmtpState state;
    private SmtpMail mail;
    private String params;

    public ReadInformationCommand(SmtpState state, String params, SmtpMail mail) {
        this.params =params;
        this.mail = mail;
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        mail.store(state, params);
        return new SmtpResponse(-1, "", state);
    }
}