package smtp.command.impl;

import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

public class DataCommand implements Command {
    private SmtpState state;

    public DataCommand(SmtpState state) {
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        if (SmtpState.RCPT != state) {
            return new SmtpResponse(503, "Bad sequence of command ", this.state);
        }
        return new SmtpResponse(354, "Start mail input; end with <CRLF>.<CRLF>", SmtpState.DATA_HDR);

    }
}