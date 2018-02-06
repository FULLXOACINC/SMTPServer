package smtp.command.impl;

import smtp.SmtpActionType;
import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

public class UnknownCommand implements Command {
    private final SmtpState state;

    public UnknownCommand(SmtpState state) {
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        return new SmtpResponse(500, "Command not recognized " + state, state);
    }
}