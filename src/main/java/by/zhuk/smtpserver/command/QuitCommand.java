package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

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