package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpResponse;


public interface Command {
    SmtpResponse execute();
}