
package smtp;

/**
 * Contains an SMTP client request. Handles state transitions using the following state transition table.
 * <PRE>
 * -----------+-------------------------------------------------------------------------------------------------
 * |                                 State
 * Action    +-------------+-----------+-----------+--------------+---------------+---------------+------------
 * | CONNECT     | GREET     | MAIL      | RCPT         | DATA_HDR      | DATA_BODY     | QUIT
 * -----------+-------------+-----------+-----------+--------------+---------------+---------------+------------
 * connect    | 220/GREET   | 503/GREET | 503/MAIL  | 503/RCPT     | 503/DATA_HDR  | 503/DATA_BODY | 503/QUIT
 * ehlo       | 503/CONNECT | 250/MAIL  | 503/MAIL  | 503/RCPT     | 503/DATA_HDR  | 503/DATA_BODY | 503/QUIT
 * mail       | 503/CONNECT | 503/GREET | 250/RCPT  | 503/RCPT     | 503/DATA_HDR  | 503/DATA_BODY | 250/RCPT
 * rcpt       | 503/CONNECT | 503/GREET | 503/MAIL  | 250/RCPT     | 503/DATA_HDR  | 503/DATA_BODY | 503/QUIT
 * data       | 503/CONNECT | 503/GREET | 503/MAIL  | 354/DATA_HDR | 503/DATA_HDR  | 503/DATA_BODY | 503/QUIT
 * data_end   | 503/CONNECT | 503/GREET | 503/MAIL  | 503/RCPT     | 250/QUIT      | 250/QUIT      | 503/QUIT
 * unrecog    | 500/CONNECT | 500/GREET | 500/MAIL  | 500/RCPT     | ---/DATA_HDR  | ---/DATA_BODY | 500/QUIT
 * quit       | 503/CONNECT | 503/GREET | 503/MAIL  | 503/RCPT     | 503/DATA_HDR  | 503/DATA_BODY | 250/CONNECT
 * blank_line | 503/CONNECT | 503/GREET | 503/MAIL  | 503/RCPT     | ---/DATA_BODY | ---/DATA_BODY | 503/QUIT
 * rset       | 250/GREET   | 250/GREET | 250/GREET | 250/GREET    | 250/GREET     | 250/GREET     | 250/GREET
 * vrfy       | 252/CONNECT | 252/GREET | 252/MAIL  | 252/RCPT     | 252/DATA_HDR  | 252/DATA_BODY | 252/QUIT
 * expn       | 252/CONNECT | 252/GREET | 252/MAIL  | 252/RCPT     | 252/DATA_HDR  | 252/DATA_BODY | 252/QUIT
 * help       | 211/CONNECT | 211/GREET | 211/MAIL  | 211/RCPT     | 211/DATA_HDR  | 211/DATA_BODY | 211/QUIT
 * noop       | 250/CONNECT | 250/GREET | 250/MAIL  | 250/RCPT     | 250|DATA_HDR  | 250/DATA_BODY | 250/QUIT
 * </PRE>
 */
class SmtpRequest {
    private SmtpActionType action;
    private SmtpState state;
    String params;

    SmtpRequest(SmtpActionType actionType, String params, SmtpState state) {
        this.action = actionType;
        this.state = state;
        this.params = params;
    }

    SmtpResponse execute() {
        SmtpResponse response;
        switch (action) {
            case EXPN: {
                response = new SmtpResponse(252, "Not supported", this.state);
                break;
            }
            case HELP: {
                response = new SmtpResponse(211, "No help available", this.state);
                break;
            }
            case CONNECT: {
                if (SmtpState.CONNECT == state) {
                    response = new SmtpResponse(220, "localhost SMTP service ready", SmtpState.GREET);
                } else {
                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
                }
                break;
            }
            case NOOP: {
                response = new SmtpResponse(250, "OK", this.state);
                break;
            }
            case VRFY: {
                response = new SmtpResponse(252, "Not supported", this.state);
                break;
            }
            case RSET: {
                response = new SmtpResponse(250, "OK", SmtpState.GREET);
                break;
            }
            case EHLO: {
                if (SmtpState.GREET == state) {
                    response = new SmtpResponse(250, "OK", SmtpState.MAIL);
                } else {
                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
                }
                break;
            }
            case MAIL: {
                if (SmtpState.MAIL == state || SmtpState.QUIT == state) {
                    response = new SmtpResponse(250, "OK", SmtpState.RCPT);
                } else {
                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
                }
                break;
            }
            case RCPT: {
                if (SmtpState.RCPT == state) {
                    response = new SmtpResponse(250, "OK", this.state);
                } else {
                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
                }
                break;
            }
            case DATA: {
                if (SmtpState.RCPT == state) {
                    response = new SmtpResponse(354, "Start mail input; end with <CRLF>.<CRLF>", SmtpState.DATA_HDR);
                } else {
                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
                }
                break;
            }
            case BLANK_LINE: {
                if (SmtpState.DATA_HDR == state) {
                    response = new SmtpResponse(-1, "", SmtpState.DATA_BODY);
                } else {
                    if (SmtpState.DATA_BODY == state) {
                        response = new SmtpResponse(-1, "", this.state);
                    } else {
                        response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
                    }
                }
                break;
            }
            case QUIT: {
                if (SmtpState.QUIT == state) {
                    response = new SmtpResponse(221, "localhost service closing transmission channel", SmtpState.CONNECT);
                } else {
                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
                }
                break;
            }
            case DATA_END: {
                if (SmtpState.DATA_HDR == state || SmtpState.DATA_BODY == state) {
                    response = new SmtpResponse(250, "OK", SmtpState.QUIT);
                } else {
                    response = new SmtpResponse(503, "Bad sequence of commands: " + action, this.state);
                }
                break;
            }
            default: {
                if (state != SmtpState.DATA_HDR && state != SmtpState.DATA_BODY) {
                    response = new SmtpResponse(500, "Command not recognized " + action + " " + state, this.state);
                }
                else {
                    response = new SmtpResponse(-1, "", this.state);
                }
            }
        }

        return response;
    }

    static SmtpRequest createRequest(String line, SmtpState state) {
        SmtpActionType action;
        String params = null;
        String request = line.toUpperCase();
        switch (state) {
            case DATA_HDR: {
                if (line.equals(".")) {
                    action = SmtpActionType.DATA_END;
                } else {
                    if (line.length() < 1) {
                        action = SmtpActionType.BLANK_LINE;
                    } else {
                        action = SmtpActionType.UNRECOG;
                        params = line;
                    }
                }
                break;
            }
            case DATA_BODY: {
                if (line.equals(".")) {
                    action = SmtpActionType.DATA_END;
                } else {
                    action = SmtpActionType.UNRECOG;
                    if (line.length() < 1) {
                        params = "\n";
                    } else {
                        params = line;
                    }
                }
                break;
            }
            default: {
                final int COMMAND_SIZE = 4;

                String commandName = request.substring(0, COMMAND_SIZE);
                switch (commandName) {
                    case "EHLO ": {
                        action = SmtpActionType.EHLO;
                        params = line.substring("EHLO ".length());
                        break;
                    }
                    case "MAIL FROM:": {
                        action = SmtpActionType.MAIL;
                        params = line.substring("MAIL FROM:".length());
                        break;
                    }
                    case "RCPT TO:": {
                        action = SmtpActionType.RCPT;
                        params = line.substring("RCPT TO:".length());
                        break;
                    }
                    default: {
                        try {
                            action = SmtpActionType.valueOf(commandName);
                        } catch (IllegalArgumentException e) {
                            action = SmtpActionType.UNRECOG;
                        }
                        break;
                    }

                }
            }
        }
        return new SmtpRequest(action, params, state);
    }
}
