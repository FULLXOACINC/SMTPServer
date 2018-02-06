package smtp.command.impl;

import smtp.SmtpActionType;
import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

public class DataEndCommand implements Command {
    private SmtpActionType actionType;
    private SmtpState state;

    public DataEndCommand(SmtpActionType actionType, SmtpState state) {
        this.actionType = actionType;
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        SmtpResponse response;
        if (SmtpState.DATA_HDR == state || SmtpState.DATA_BODY == state) {
            response = new SmtpResponse(250, "OK", SmtpState.QUIT);
        } else {
            response = new SmtpResponse(503, "Bad sequence of commands: " + actionType, this.state);
        }
        return response;
    }
}