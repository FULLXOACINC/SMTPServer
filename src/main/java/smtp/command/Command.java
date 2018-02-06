package smtp.command;

import smtp.SmtpResponse;


public interface Command {
    SmtpResponse execute();
}