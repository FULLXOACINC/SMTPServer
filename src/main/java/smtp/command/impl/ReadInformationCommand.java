package smtp.command.impl;

import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

public class ReadInformationCommand implements Command {
    private SmtpState state;

    public ReadInformationCommand(SmtpState state) {
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        return new SmtpResponse(-1, "", state);
    }
}