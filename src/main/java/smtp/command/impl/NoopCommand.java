package smtp.command.impl;

import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

public class NoopCommand implements Command {
    private SmtpState state;

    public NoopCommand(SmtpState state) {
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        return new SmtpResponse(250, "OK", state);
    }
}