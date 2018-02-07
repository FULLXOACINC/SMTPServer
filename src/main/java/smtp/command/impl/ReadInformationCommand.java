package smtp.command.impl;

import smtp.SmtpMail;
import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

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