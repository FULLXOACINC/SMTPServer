package smtp.command.impl;

import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

public class HelpCommand implements Command {
    private SmtpState state;
    private String params;

    public HelpCommand(SmtpState state, String params) {
        this.params = params;
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        return null;
    }
}