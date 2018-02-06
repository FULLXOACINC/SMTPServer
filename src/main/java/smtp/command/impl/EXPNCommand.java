package smtp.command.impl;

import smtp.SmtpResponse;
import smtp.SmtpState;
import smtp.command.Command;

import java.io.File;
import java.util.Arrays;

public class EXPNCommand implements Command {
    private static final String POST_PATH="post";
    private SmtpState state;

    public EXPNCommand(SmtpState state) {
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        File[] directories = new File(POST_PATH+"/").listFiles(File::isDirectory);
        return new SmtpResponse(250, "All clients:"+ Arrays.toString(directories), this.state);
    }
}