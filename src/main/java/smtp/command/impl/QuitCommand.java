package smtp.command.impl;

import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

public class QuitCommand implements Command {
    private SmtpState state;

    public QuitCommand(SmtpState state) {
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        if (SmtpState.QUIT != state) {
            return new SmtpResponse(503, "Bad sequence of command ", this.state);
        }
        return new SmtpResponse(221, "localhost service closing transmission channel", SmtpState.CONNECT);

    }
}