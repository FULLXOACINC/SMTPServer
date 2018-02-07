package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

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