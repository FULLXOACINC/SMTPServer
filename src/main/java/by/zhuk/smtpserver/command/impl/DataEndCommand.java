package by.zhuk.smtpserver.command.impl;


import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpMail;
import by.zhuk.smtpserver.smtp.SmtpState;
import by.zhuk.smtpserver.command.Command;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

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
        String mailId = mail.findMailId();
        if (mailId == null) {
            mailId = mail.toString();
        }
        for (String receiver : mail.getReceivers()) {
            try (PrintWriter out = new PrintWriter(mail.getSendType() + "/" + receiver + "/" + mailId)) {
                out.println(mail.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}