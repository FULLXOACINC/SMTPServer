package by.zhuk.smtpserver.keeper.impl;

import by.zhuk.smtpserver.keeper.Keeper;
import by.zhuk.smtpserver.smtp.SmtpMail;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TerminalKeeper implements Keeper{
    private static String KEEP_PATH="terminal";
    @Override
    public void save(SmtpMail mail) {
        String mailId = mail.findMailId();
        if (mailId == null) {
            mailId = mail.toString();
        }
        for (String receiver : mail.getReceivers()) {
            try (PrintWriter out = new PrintWriter(KEEP_PATH + "/" + receiver + "/" + mailId)) {
                out.println(mail.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}