
package smtp;

import smtp.command.Command;
import smtp.command.impl.ConnectCommand;
import smtp.command.impl.DataCommand;
import smtp.command.impl.DataEndCommand;
import smtp.command.impl.EXPNCommand;
import smtp.command.impl.HelloCommand;
import smtp.command.impl.HelpCommand;
import smtp.command.impl.MailCommand;
import smtp.command.impl.NoopCommand;
import smtp.command.impl.QuitCommand;
import smtp.command.impl.RCPTCommand;
import smtp.command.impl.ReadInformationCommand;
import smtp.command.impl.ResetCommand;
import smtp.command.impl.VRFYCommand;

class SmtpRequest {
    private SmtpActionType action;
    private SmtpState state;
    String params;

    SmtpRequest(SmtpActionType actionType, String params, SmtpState state) {
        this.action = actionType;
        this.state = state;
        this.params = params;
    }

//    SmtpResponse execute() {
//        SmtpResponse response;
//        switch (action) {
//            case EXPN: {
//                response = new SmtpResponse(252, "Not supported", this.state);
//                break;
//            }
////            case SEND: {
////                response = new SmtpResponse(252, "Not supported", this.state);
////                break;
////            }
////            case SOML: {
////                response = new SmtpResponse(252, "Not supported", this.state);
////                break;
////            }
////            case SAML: {
////                response = new SmtpResponse(252, "Not supported", this.state);
////                break;
////            }
//            case HELP: {
//                response = new SmtpResponse(211, "No help available", this.state);
//                break;
//            }
//            case CONNECT: {
//                if (SmtpState.CONNECT == state) {
//                    response = new SmtpResponse(220, "localhost SMTP service ready", SmtpState.GREET);
//                } else {
//                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
//                }
//                break;
//            }
//            case NOOP: {
//                response = new SmtpResponse(250, "OK", this.state);
//                break;
//            }
//            case VRFY: {
//                response = new SmtpResponse(252, "Not supported", this.state);
//                break;
//            }
//            case RSET: {
//                response = new SmtpResponse(250, "OK", SmtpState.GREET);
//                break;
//            }
//            case EHLO: {
//                if (SmtpState.GREET == state) {
//                    response = new SmtpResponse(250, "OK", SmtpState.MAIL);
//                } else {
//                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
//                }
//                break;
//            }
//            case MAIL: {
//                if (SmtpState.MAIL == state || SmtpState.QUIT == state) {
//                    response = new SmtpResponse(250, "OK", SmtpState.RCPT);
//                } else {
//                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
//                }
//                break;
//            }
//            case RCPT: {
//                if (SmtpState.RCPT == state) {
//                    response = new SmtpResponse(250, "OK", this.state);
//                } else {
//                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
//                }
//                break;
//            }
//            case DATA: {
//                if (SmtpState.RCPT == state) {
//                    response = new SmtpResponse(354, "Start mail input; end with <CRLF>.<CRLF>", SmtpState.DATA_HDR);
//                } else {
//                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
//                }
//                break;
//            }
//            case BLANK_LINE: {
//                if (SmtpState.DATA_HDR == state) {
//                    response = new SmtpResponse(-1, "", SmtpState.DATA_BODY);
//                } else {
//                    if (SmtpState.DATA_BODY == state) {
//                        response = new SmtpResponse(-1, "", this.state);
//                    } else {
//                        response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
//                    }
//                }
//                break;
//            }
//            case QUIT: {
//                if (SmtpState.QUIT == state) {
//                    response = new SmtpResponse(221, "localhost service closing transmission channel", SmtpState.CONNECT);
//                } else {
//                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
//                }
//                break;
//            }
//            case DATA_END: {
//                if (SmtpState.DATA_HDR == state || SmtpState.DATA_BODY == state) {
//                    response = new SmtpResponse(250, "OK", SmtpState.QUIT);
//                } else {
//                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
//                }
//                break;
//            }
//            default: {
//                if (state != SmtpState.DATA_HDR && state != SmtpState.DATA_BODY) {
//                    response = new SmtpResponse(500, "Command not recognized " + action + " " + state, this.state);
//                } else {
//                    response = new SmtpResponse(-1, "", this.state);
//                }
//            }
//        }
//
//        return response;
//    }

    static Command findCommand(String line, SmtpState state, SmtpMail mail) {
        Command command = null;
        String params;
        String request = line.toUpperCase();
        if (state == SmtpState.DATA_HDR) {

            if (line.length() < 1) {
                command = new ReadInformationCommand(SmtpState.DATA_BODY, "", mail);
            } else {
                params = line;
                command = new ReadInformationCommand(state, params, mail);
            }

            return command;
        }
        if (state == SmtpState.DATA_BODY) {
            if (line.equals(".")) {
                command = new DataEndCommand(state, mail);
            } else {
                if (line.length() < 1) {
                    command = new ReadInformationCommand(SmtpState.DATA_BODY, "", mail);
                } else {
                    params = line;
                    command = new ReadInformationCommand(state, params, mail);
                }
            }
            return command;
        }


        final int COMMAND_SIZE = 4;

        String commandName = request.substring(0, COMMAND_SIZE);
        switch (commandName) {
            case "EHLO": {
                command = new HelloCommand(state);
                break;
            }
            case "VRFY": {
                params = line.substring("VRFY ".length());
                command = new VRFYCommand(state, params);
                break;
            }
            case "MAIL": {
                params = line.substring("MAIL FROM:".length());
                command = new MailCommand(mail, params, state);
                break;
            }
            case "RCPT": {
                params = line.substring("RCPT TO:".length());
                command = new RCPTCommand(mail, params, state);
                break;
            }
            case "HELP": {
                params = line.substring("HELP ".length());
                command = new HelpCommand(state, params);
                break;
            }
            case "EXPN": {
                command = new EXPNCommand(state);
                break;
            }
            case "NOOP": {
                command = new NoopCommand(state);
                break;
            }
            case "RSET": {
                command = new ResetCommand(mail);
                break;
            }
            case "CONN": {
                command = new ConnectCommand(state);
                break;
            }
            case "QUIT": {
                command = new QuitCommand(state);
                break;
            }
            case "DATA": {
                command = new DataCommand(state);
                break;
            }
        }
        return command;
    }
}
