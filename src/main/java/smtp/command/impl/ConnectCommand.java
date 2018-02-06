package smtp.command.impl;

import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

public class ConnectCommand implements Command {
    private final SmtpState state;

    public ConnectCommand(SmtpState state) {
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        if (SmtpState.CONNECT == state) {
            return new SmtpResponse(220, "localhost SMTP service ready", SmtpState.GREET);
        } else {
            return new SmtpResponse(503, "Bad sequence of command", this.state);
        }
    }
}