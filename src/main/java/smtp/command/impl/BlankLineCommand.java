package smtp.command.impl;

import smtp.SmtpResponse;
import smtp.command.Command;

public class BlankLineCommand implements Command {
    @Override
    public SmtpResponse execute() {
        return null;
    }
}