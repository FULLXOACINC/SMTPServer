package smtp.command.impl;

import smtp.SmtpActionType;
import smtp.SmtpMail;
import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

public class DataEndCommand implements Command {
    private SmtpMail mail;
    private SmtpState state;

    public DataEndCommand(SmtpState state, SmtpMail mail) {
        this.mail = mail;
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        SmtpResponse response;
        if (SmtpState.DATA_HDR == state || SmtpState.DATA_BODY == state) {
            response = new SmtpResponse(250, "OK", SmtpState.QUIT);
        } else {
            response = new SmtpResponse(503, "Bad sequence of command", this.state);
        }
        save(mail);
        mail.clear();
        return response;
    }

    private void save(SmtpMail mail) {

    }
}