package by.zhuk.smtpserver.command;


import by.zhuk.smtpserver.keeper.Keeper;
import by.zhuk.smtpserver.smtp.SmtpMail;
import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

import java.util.List;

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
        List<Keeper> keepers = mail.getKeepers();
        for (Keeper keeper : keepers) {
            keeper.save(mail);
        }
        mail.clear();
        return response;
    }

}