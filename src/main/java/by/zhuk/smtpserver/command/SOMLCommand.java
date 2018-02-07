package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.keeper.PostKeeper;
import by.zhuk.smtpserver.keeper.TerminalKeeper;
import by.zhuk.smtpserver.smtp.SmtpMail;
import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

public class SOMLCommand implements Command{
    private SmtpMail mail;
    private String params;
    private SmtpState state;

    public SOMLCommand(SmtpMail mail, String params, SmtpState state) {
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
        if(mail.getReceivers().size()!=1){
            mail.addKeeper(new TerminalKeeper());
        }
        else {
            mail.addKeeper(new PostKeeper());
        }

        return new SmtpResponse(250, "OK", SmtpState.RCPT);
    }
}