package by.zhuk.smtpserver.command.impl;

import by.zhuk.smtpserver.command.Command;
import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;
import by.zhuk.smtpserver.validater.ClientNameValidator;

public class VRFYCommand implements Command {
    private SmtpState state;
    private String params;

    public VRFYCommand(SmtpState state, String params) {
        this.params = params;
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        if (!ClientNameValidator.validateParams(params)) {
            return new SmtpResponse(503, "NOt valid parameter", this.state);
        }
        String client = params.substring(1, params.length() - 1);
        if (ClientNameValidator.isClientExist(client)) {
            return new SmtpResponse(250, "Client is exist", state);
        } else {
            return new SmtpResponse(252, "Client is not exist", state);
        }
    }
}