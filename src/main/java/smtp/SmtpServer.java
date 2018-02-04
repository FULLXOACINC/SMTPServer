package smtp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class SmtpServer {

    public static final int DEFAULT_SMTP_PORT = 1337;

    private static final Pattern CRLF = Pattern.compile("\r\n");

    private final List<SmtpMessage> receivedMail;

    private final ServerSocket serverSocket;

    private final Thread workerThread;

    private volatile boolean stopped = false;

    public static SmtpServer start(int port) throws IOException {
        return new SmtpServer(new ServerSocket(port));
    }

    private SmtpServer(ServerSocket serverSocket) {
        this.receivedMail = new ArrayList<>();
        this.serverSocket = serverSocket;
        this.workerThread = new Thread(this::performWork);
        this.workerThread.start();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public List<SmtpMessage> getReceivedEmails() {
        synchronized (receivedMail) {
            return Collections.unmodifiableList(new ArrayList<>(receivedMail));
        }
    }

    public void reset() {
        synchronized (receivedMail) {
            receivedMail.clear();
        }
    }

    public void stop() {
        if (stopped) {
            return;
        }
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
        }

    }

    private void performWork() {
        try {
            while (!stopped) {
                Socket socket = serverSocket.accept();
                try (Scanner input = new Scanner(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)).useDelimiter(CRLF);
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));) {

                    synchronized (receivedMail) {
                        receivedMail.addAll(handleTransaction(out, input));
                    }
                }
                socket.close();
            }
        } catch (Exception e) {
            if (!stopped) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    private static List<SmtpMessage> handleTransaction(PrintWriter out, Iterator<String> input) {
        SmtpState smtpState = SmtpState.CONNECT;
        SmtpRequest smtpRequest = new SmtpRequest(SmtpActionType.CONNECT, "", smtpState);

        SmtpResponse smtpResponse = smtpRequest.execute();

        sendResponse(out, smtpResponse);
        smtpState = smtpResponse.getNextState();

        List<SmtpMessage> msgList = new ArrayList<>();
        SmtpMessage msg = new SmtpMessage();

        while (smtpState != SmtpState.CONNECT) {
            String line = input.next();
            System.out.println(line);
            if (line == null) {
                break;
            }

            SmtpRequest request = SmtpRequest.createRequest(line, smtpState);
            SmtpResponse response = request.execute();
            smtpState = response.getNextState();
            sendResponse(out, response);

            String params = request.params;
            msg.store(response, params);


            if (smtpState == SmtpState.QUIT) {
                msgList.add(msg);
                msg = new SmtpMessage();
            }
        }

        return msgList;
    }

    private static void sendResponse(PrintWriter out, SmtpResponse smtpResponse) {
        if (smtpResponse.getCode() > 0) {
            int code = smtpResponse.getCode();
            String message = smtpResponse.getMessage();
            out.print(code + " " + message + "\r\n");
            out.flush();
        }
    }
}
