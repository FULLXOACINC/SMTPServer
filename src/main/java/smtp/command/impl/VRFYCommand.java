package smtp.command.impl;

import smtp.SmtpActionType;
import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

public class VRFYCommand implements Command {
    @Override
    public SmtpResponse execute(SmtpActionType action, SmtpState state, String params) {
        return null;
    }
}