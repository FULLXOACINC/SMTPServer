package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

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