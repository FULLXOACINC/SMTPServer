
package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpMail;
import by.zhuk.smtpserver.smtp.SmtpState;


public class CommandFactory {


    public static Command findCommand(String line, SmtpState state, SmtpMail mail) {
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
            case "SEND": {
                params = line.substring("SEND FROM:".length());
                command = new SendCommand(mail, params, state);
                break;
            }
            case "SOML": {
                params = line.substring("SOML FROM:".length());
                command = new SOMLCommand(mail, params, state);
                break;
            }
            case "SAML": {
                params = line.substring("SAML FROM:".length());
                command = new SAMLCommand(mail, params, state);
                break;
            }
            case "RCPT": {
                params = line.substring("RCPT TO:".length());
                command = new RCPTCommand(mail, params, state);
                break;
            }
            case "HELP": {
                if ("HELP ".length() <= line.length()) {
                    command = new UnknownCommand(state);
                }
                else {
                    params = line.substring("HELP ".length());
                    command = new HelpCommand(state, params);
                }
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
