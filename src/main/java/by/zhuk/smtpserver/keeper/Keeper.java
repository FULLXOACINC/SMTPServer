package by.zhuk.smtpserver.keeper;

import by.zhuk.smtpserver.smtp.SmtpMail;

public interface Keeper {
    void save(SmtpMail mail);
}