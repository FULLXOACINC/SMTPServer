package by.zhuk.smtpserver.keeper;

import by.zhuk.smtpserver.smtp.SmtpMail;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class PostKeeper implements Keeper {
    private static String KEEP_PATH="post";
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