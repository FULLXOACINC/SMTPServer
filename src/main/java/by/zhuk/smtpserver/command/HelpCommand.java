package by.zhuk.smtpserver.command;

import by.zhuk.smtpserver.smtp.SmtpResponse;
import by.zhuk.smtpserver.smtp.SmtpState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelpCommand implements Command {
    private static final String PATH = "commands/commands.txt";
    private SmtpState state;
    private String params;

    public HelpCommand(SmtpState state, String params) {
        this.params = params;
        this.state = state;
    }

    @Override
    public SmtpResponse execute() {
        List<String> list = new ArrayList<>();
        final int COMMAND_SIZE = 4;
        if (params.length() < COMMAND_SIZE) {
            return new SmtpResponse(502, "Command not supported ", state);
        }
        String command = params.substring(1, params.length() - 1);

        try (Stream<String> stream = Files.lines(Paths.get(PATH))) {
            list = stream
                    .filter(line -> line.startsWith(command))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.isEmpty()) {
            return new SmtpResponse(502, "Command not supported ", state);
        }

        return new SmtpResponse(214, list.get(0), state);

    }
}